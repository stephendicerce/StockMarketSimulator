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
        user VARCHAR(40),
        company VARCHAR(40),
        number INT,
        PRIMARY KEY (user, company)
    );
    GRANT ALL ON stocksim.* TO 'stocksimuser' IDENTIFIED BY 'stocksimpassword';
    INSERT INTO Companies VALUES('Company0', 50, 100);
    INSERT INTO Companies VALUES('Company1', 50, 100);
    INSERT INTO Companies VALUES('Company2', 50, 100);
    INSERT INTO Companies VALUES('Company3', 50, 100);
    INSERT INTO Companies VALUES('Company4', 50, 100);
    INSERT INTO Companies VALUES('Company5', 50, 100);
    INSERT INTO Companies VALUES('Company6', 50, 100);
    INSERT INTO Companies VALUES('Company7', 50, 100);
    INSERT INTO Companies VALUES('Company8', 50, 100);
    INSERT INTO Companies VALUES('Company9', 50, 100);
```

Once the SQL database has been set up, run the application.
