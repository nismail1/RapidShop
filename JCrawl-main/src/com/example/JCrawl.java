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
import javafx.util.Callback;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JCrawl extends Application {

    private Set<String> visitedURLs;
    private int visitCount;
    private ObservableList<Image> imagesList;
    private WebDriver driver;

    public JCrawl() {
        visitedURLs = new HashSet<>();
        visitCount = 0;
        imagesList = FXCollections.observableArrayList();
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in headless mode
        driver = new ChromeDriver(options);
    }

    public void crawl(String startURL) {
        crawlURL(startURL);
        driver.quit(); // Close the WebDriver after crawling
    }

    private void crawlURL(String urlString) {
        if (visitedURLs.contains(urlString) || visitCount >= 20) {
            return;
        }

        visitedURLs.add(urlString);
        visitCount++;
        System.out.println("Visiting URL: " + urlString);

        try {
            if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
                System.out.println("Skipping non-HTTP/HTTPS URL: " + urlString);
                return;
            }

            driver.get(urlString);
            String pageSource = driver.getPageSource();
            Document doc = Jsoup.parse(pageSource);

            // Extract images
            Elements imgElements = doc.getElementsByTag("img");
            for (Element img : imgElements) {
                String imgUrl = img.absUrl("src");
                if (!imgUrl.isEmpty()) {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Web Crawler Images");

        ListView<ImageView> listView = new ListView<>();
        listView.setPrefSize(600, 400);

        imagesList.forEach(image -> {
            System.out.println("Image URL: " + image.getUrl());
        });

        ObservableList<ImageView> imageViewList = imagesList.stream()
                .map(image -> new ImageView(image))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        listView.setItems(imageViewList);

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
