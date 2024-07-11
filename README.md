# RapidShop

RapidShop is an e-commerce web application designed to provide a seamless online shopping experience. This project is developed using Java and the Spring Boot framework, with a front-end powered by Thymeleaf, Bootstrap, and jQuery.

## Features

- **User Authentication**: Secure registration and login system.
- **Product Management**: Add, edit, and delete products.
- **Shopping Cart**: Add products to the cart and manage cart items.
- **Order Processing**: Place orders and view order history.
- **Admin Dashboard**: Manage users, products, and orders.

## Technologies Used

- **Backend**: Java, Spring Boot
- **Frontend**: Thymeleaf, Bootstrap, jQuery
- **Database**: MySQL
- **Build Tool**: Maven

## Getting Started

### Prerequisites

- [Java JDK](https://www.oracle.com/java/technologies/javase-downloads.html)
- [Maven](https://maven.apache.org/download.cgi)
- [MySQL](https://www.mysql.com/downloads/)

### Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/nismail1/RapidShop.git
    ```
2. Navigate to the project directory:
    ```bash
    cd RapidShop
    ```
3. Update the database configuration in `src/main/resources/application.properties`:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/rapidshop
    spring.datasource.username=yourusername
    spring.datasource.password=yourpassword
    ```
4. Build the project:
    ```bash
    mvn clean install
    ```
5. Run the application:
    ```bash
    mvn spring-boot:run
    ```

### Running the Tests

To run the tests, use the following command:
```bash
mvn test
