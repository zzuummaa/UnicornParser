import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Parse {
    public static void main(String[] args) throws IOException {
        String imageDir = "images/";
        String pinId = "705024516639053729";
        PinterestPareser pareser = new PinterestPareser();

        ArrayList<ImageData> images = pareser.extractImages(pinId);
        List<String> alreadyDownload = Files
                .list(Paths.get(imageDir))
                .map(path -> {
                    String fileName = path.getFileName().toString();
                    return fileName.substring(0, fileName.lastIndexOf("."));
                })
                .collect(Collectors.toList());



        for (ImageData data : images) {
            if (alreadyDownload.contains(data.getId())) {
                System.out.println("Image " + data.getId() + " skipped: already exists");
                continue;
            }

            System.out.println("Download image " + data.getUrl());
            BufferedImage image = ImageIO.read(new URL(data.getUrl()));
            File file = new File(imageDir + data.getId() + ".jpg");
            if (!file.exists()) ImageIO.write(image, "jpg", file);
        }
    }

}
