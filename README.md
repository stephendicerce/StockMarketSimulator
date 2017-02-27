# StockMarketSimulator
A stock market simulator application (and much more) designed and built for my Web Services course at SUNY Oswego in collaboration with fellow student Stephen Dicerce.

To run the application, you must have MySQL running on port 3306 of the same machine, the calls use localhost. Once MySQL is installed, and in your path, enter into the command prompt:
```
    mysql -u root -p
    CREATE DATABASE stocksim;
    USE stocksim;
    CREATE TABLE Users (
        name VARCHAR(40),
        password VARCHAR(40),
        money DOUBLE PRECISION,
        PRIMARY KEY (name)
    );
    CREATE TABLE Companies (
        name VARCHAR(40),
        stockValue DOUBLE PRECISION,
        availableStocks INT,
        PRIMARY KEY (name)
    );
    CREATE TABLE Stocks (
        name VARCHAR(40),
        company VARCHAR(40),
        number INT,
        PRIMARY KEY (name, company)
    );
```

Once the SQL database has been set up, run the application.
