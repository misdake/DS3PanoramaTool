package com.rs.app.ds3.panoramatool;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    private static final File screenshotFolder = new File("Screenshots");

    public static void main(String[] args) throws IOException {

        String[] names = {
                "px.jpg",
                "nx.jpg",
                "py.jpg",
                "ny.jpg",
                "pz.jpg",
                "nz.jpg",
        };

        File[] screenshots = screenshotFolder.listFiles((file, s) -> s.startsWith("DarkSoulsIII ") && s.endsWith(".png"));
        if (screenshots != null && screenshots.length == 6) {
            for (int i = 0; i < 6; i++) {
                File file = screenshots[i];
                BufferedImage image = ImageIO.read(file);
                if (image.getWidth() == 1920 && image.getHeight() == 1080) {
                    BufferedImage subimage = image.getSubimage(448, 28, 1024, 1024);
                    if (i == 2 || i == 3) {
                        subimage = rotate(subimage, -Math.PI / 2);
                    }
                    ImageIO.write(subimage, "jpg", new File(file.getParent() + "\\" + names[i]));
                }
            }
        }
    }

    private static BufferedImage rotate(BufferedImage src, double radians) {
        BufferedImage newImage = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        Graphics2D g2 = newImage.createGraphics();
        g2.rotate(radians, src.getWidth() / 2, src.getHeight() / 2);
        g2.drawImage(src, null, 0, 0);
        return newImage;
    }

}
