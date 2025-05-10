Use your terminal to make a database called "travel_booking_db". The name should should match exactly.

✅ 1. Log In to MySQL as Root

mysql -u root -p

✅ 2. Create a New Database

CREATE DATABASE travel_booking_db;

✅ 3. Create an Admin User

CREATE USER 'admin'@'localhost' IDENTIFIED BY 'mytravels';

✅ 4. Grant Privileges to the Admin User

GRANT ALL PRIVILEGES ON travel_booking_db.* TO 'admin'@'localhost';

✅ 5. Apply the Privileges

FLUSH PRIVILEGES;

✅ 6. Exit MySQL

EXIT;

✅ 7. Log In as Admin to Verify

mysql -u admin -p

USE travel_booking_db;


run the following commands to start the app
1. mvn clean package  ->  (always run after making changes to a file to update the build)
2. java -jar target/sql-table-editor-1.0-SNAPSHOT-jar-with-dependencies.jar  ->   (to run the app)