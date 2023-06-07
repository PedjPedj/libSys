# libSys

libSys is a Java program developed for a school project, aimed at creating a "Library System." Please note that this project is unfinished and unlikely to be completed.

## Dependencies

To run this program, you need to install the following dependencies:

- MySQL
- Java
- The following libraries:
  - Ikonli-core-12.3.1
  - Ikonli-swing-12.3.1
  - Ikonli-material-design-pack-12.3.1
  - mysql-connector-j-8.0.33
  - jcalendar-1.4

## Database Creation

To set up the required database structure, execute the following SQL statements:

(Personally, I used sql workbench for creating the database)


```sql
CREATE DATABASE mylibraryapp /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci / /!80016 DEFAULT ENCRYPTION='N' */;

CREATE TABLE books (
  id varchar(40) NOT NULL,
  Name varchar(100) DEFAULT NULL,
  Author varchar(100) DEFAULT NULL,
  Quantity int DEFAULT NULL,
  publication varchar(100) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE borrowrecords (
  borrow_id varchar(45) NOT NULL,
  user_id varchar(40) NOT NULL,
  book_id varchar(40) DEFAULT NULL,
  quantity int DEFAULT NULL,
  borrow_date date DEFAULT NULL,
  return_date date DEFAULT NULL,
  isReturned tinyint(1) DEFAULT NULL,
  PRIMARY KEY (borrow_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE sysadmins (
  admin_ID varchar(40) NOT NULL,
  username varchar(50) DEFAULT NULL,
  password varchar(100) DEFAULT NULL,
  name varchar(100) DEFAULT NULL,
  email varchar(100) DEFAULT NULL,
  PRIMARY KEY (admin_ID),
  UNIQUE KEY username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE users (
  user_ID varchar(40) NOT NULL,
  name varchar(50) DEFAULT NULL,
  email varchar(100) DEFAULT NULL,
  PRIMARY KEY (user_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

Please make sure you have the necessary permissions to create the database and tables.

For Sql handling stuff please check MySQLHandler.java.

The `MySQLHandler.java` file can be found under the `Utils` folder. This file provides the necessary utility functions for handling MySQL database operations.

This program is provided as-is and may contain bugs or incomplete features. Feel free to modify and build upon it according to your requirements.