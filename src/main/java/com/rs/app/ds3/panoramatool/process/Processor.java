package com.rs.app.ds3.panoramatool.process;

import com.rs.app.ds3.panoramatool.data.Point;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class Processor {

    private final File sourceFolder;//= new File("C:\\Software\\Fraps\\Screenshots");
    private final File targetFolder;//= new File("C:\\Project_git\\DS3PanoramaViewer\\texture");

    public Processor(File sourceFolder, File targetFolder) {
        this.sourceFolder = sourceFolder;
        this.targetFolder = targetFolder;
    }

    public double[] readClipboardXYZ() {
        try {
            String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            String[] xyz = data.split(",");
            double x = Double.parseDouble(xyz[0]);
            double y = Double.parseDouble(xyz[1]);
            double z = Double.parseDouble(xyz[2]);
            return new double[]{x, y, z};
        } catch (Exception e) {
            return null;
        }
    }

    public void process(Point point) {
        File[] screenshots = sourceFolder.listFiles((file, s) -> s.startsWith("DarkSoulsIII ") && (s.endsWith(".jpg") || s.endsWith(".png")));
        if (screenshots != null && screenshots.length == 6) {
            String folder = targetFolder.getAbsolutePath() + "/" + point.mapId + "/" + point.id;
            new File(folder).mkdirs();
            Arrays.sort(screenshots, Comparator.comparing(File::getName));
            for (int i = 0; i < 6; i++) {
                File file = screenshots[i];
                try {
                    BufferedImage image = ImageIO.read(file);
                    if (image.getWidth() == 1920 && image.getHeight() == 1080) {
                        BufferedImage subimage = image.getSubimage(448, 28, 1024, 1024);
                        if (i == 4) {
                            subimage = rotate(subimage, -Math.PI);
                        }
                        writeImage(subimage, new File(folder + "/" + Point.TEXTURE_NAMES[i]));
                        writeImage(subimage, new File(targetFolder.getAbsolutePath() + "/" + Point.TEXTURE_NAMES[i]));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for (File screenshot : screenshots) {
                screenshot.delete();
            }
        }
    }

    private BufferedImage rotate(BufferedImage src, double radians) {
        BufferedImage newImage = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        Graphics2D g2 = newImage.createGraphics();
        g2.rotate(radians, src.getWidth() * 0.5, src.getHeight() * 0.5);
        g2.drawImage(src, null, 0, 0);
        return newImage;
    }
    private JPEGImageWriteParam jpegParams;
    {
        jpegParams = new JPEGImageWriteParam(null);
        jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpegParams.setCompressionQuality(0.9f);
    }

    private void writeImage(BufferedImage image, File file) throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        FileImageOutputStream output = new FileImageOutputStream(file);
        writer.setOutput(output);
        writer.write(null, new IIOImage(image, null, null), jpegParams);
        output.close();
    }

}
