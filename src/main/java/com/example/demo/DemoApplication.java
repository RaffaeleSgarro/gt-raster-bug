package com.example.demo;

import org.geotools.coverage.CoverageFactoryFinder;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.process.raster.PolygonExtractionProcess;
import org.jaitools.numeric.Range;
import org.locationtech.jts.geom.Geometry;
import org.opengis.util.ProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class DemoApplication {

    private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Component
    public static class TriggerBug implements ApplicationRunner {

        @Override
        public void run(ApplicationArguments args) throws Exception {

            BufferedImage raster = ImageIO.read(getClass().getResource("/images/image-01.png"));

            PolygonExtractionProcess extraction = new PolygonExtractionProcess();
            GridCoverageFactory coverageFactory = CoverageFactoryFinder.getGridCoverageFactory(null);
            GridCoverage2D coverage = coverageFactory.create(
                    "coverage",
                    raster,
                    new ReferencedEnvelope(
                            new Rectangle2D.Double(
                                    raster.getMinX(),
                                    raster.getMinY(),
                                    raster.getWidth(),
                                    raster.getHeight()
                            ),
                            null
                    )
            );

            boolean insideEdges = false;
            List<Number> noDataValues = Collections.singletonList(-1);
            List<Range> classificationRanges = Collections.singletonList(new Range<>(250, true, 255, true));
            int band = 0;
            Geometry roi = null;
            ProgressListener progressListener = null;

            SimpleFeatureCollection featureCollection = extraction.execute(
                    coverage,
                    band,
                    insideEdges,
                    roi,
                    noDataValues,
                    classificationRanges,
                    progressListener
            );

            try (SimpleFeatureIterator it = featureCollection.features()) {
                while (it.hasNext()) {
                    Object defaultGeometry = it.next().getDefaultGeometry();
                    log.info("Found {}", defaultGeometry);
                }
            }
        }
    }
}
