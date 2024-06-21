package com.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class JCrawl extends Application {

    private Set<String> visitedURLs;
    private int visitCount;
    private ObservableList<Image> imagesList;

    public JCrawl() {
        visitedURLs = new HashSet<>();
        visitCount = 0;
        imagesList = FXCollections.observableArrayList();
    }

    public void crawl(String startURL) {
        crawlURL(startURL);
    }

    private void crawlURL(String urlString) {
        // Check if URL is already visited or if visit count is greater than 20
        if (visitedURLs.contains(urlString) || visitCount >= 20) {
            return;
        }

        visitedURLs.add(urlString);
        visitCount++;
        System.out.println("Visiting URL: " + urlString);

        try {
            // Check if URL starts with 'http://' or 'https://'
            if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
                System.out.println("Skipping non-HTTP/HTTPS URL: " + urlString);
                return;
            }

            // Connect to the URL and get the response
            Connection.Response response = Jsoup.connect(urlString)
                                                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                                                .ignoreContentType(true) // Ignore unsupported MIME types
                                                .execute();

            // Check if content type is supported (text/*, */xml, or */*+xml)
            if (!response.contentType().startsWith("text/") && !response.contentType().endsWith("+xml")) {
                System.out.println("Skipping URL with unsupported content type: " + urlString);
                return;
            }

            // Parse the document
            Document doc = response.parse();

            // Extract images
            Elements imgElements = doc.getElementsByTag("img");
            for (Element img : imgElements) {
                // Get the absolute URL of the image
                String imgUrl = img.absUrl("src");
                if (!imgUrl.isEmpty()) { // Validate URL before creating Image object
                    try {
                        Image image = new Image(imgUrl, true);
                        imagesList.add(image);
                        System.out.println("Found image URL: " + imgUrl);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Failed to load image from URL: " + imgUrl);
                        e.printStackTrace();
                    }
                }
            }

            // Extract links and recursively crawl
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String nextURL = link.absUrl("href");
                crawlURL(nextURL);
            }

        } catch (HttpStatusException e) {
            System.err.println("HTTP error fetching URL. Status=" + e.getStatusCode() + ", URL=" + e.getUrl());
        } catch (UnsupportedMimeTypeException e) {
            System.err.println("Unsupported MIME type. Skipping URL: " + urlString);
        } catch (IOException e) {
            System.err.println("IOException occurred while connecting to URL: " + urlString);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Web Crawler Images");
    
        ListView<ImageView> listView = new ListView<>();
        listView.setPrefSize(600, 400);
    
        // Print out image URLs for debugging
        imagesList.forEach(image -> {
            System.out.println("Image URL: " + image.getUrl());
        });
    
        // Convert imagesList to ObservableList<ImageView>
        ObservableList<ImageView> imageViewList = imagesList.stream()
                .map(image -> new ImageView(image))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        listView.setItems(imageViewList);
    
        // Custom ListCell to display ImageView items
        listView.setCellFactory(new Callback<ListView<ImageView>, ListCell<ImageView>>() {
            @Override
            public ListCell<ImageView> call(ListView<ImageView> param) {
                return new ListCell<ImageView>() {
                    @Override
                    protected void updateItem(ImageView item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setGraphic(item);
                        }
                    }
                };
            }
        });
    
        VBox vBox = new VBox(listView);
        Scene scene = new Scene(vBox, 600, 400);
    
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        JCrawl crawler = new JCrawl();
        crawler.crawl("https://www.wikipedia.org/");

        launch(args); // Launch JavaFX application
    }
}
