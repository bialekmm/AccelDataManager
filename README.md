# AccelDataManager

Accel Data Manager is a comprehensive vibration data analysis application. It enables users to efficiently manage data and perform advanced analyses. The application is based on Java using Spring. Data storage is done using PostgreSQL.

## Key Features

**Intuitive User Interface:**
   - Create a user-friendly interface for seamless data transmission and analysis result presentation.

**Time Series Plotting:**
   - Provide functionality for plotting time series data, allowing users to visualize registered vibration patterns.

**Statistical Analysis:**
   - Conduct in-depth statistical analysis of the collected data, offering users a deeper understanding of the analyzed information.

**Frequency Analysis:**
   - Perform frequency analysis of the signal, aiding in the interpretation and comprehension of the data.

**Security Measures:**
   - Implement robust login and authorization mechanisms to ensure data security and user access control.

**Multiple Data Sources Support:**
   - Accommodate different types of vibration data sources, utilizing two measurement paths and various accelerometer types for data collection.

**Data Collection Capability:**
   - Enable the application to collect measurement data effectively, supporting adaptability to diverse types of incoming data.

**Channel Data Manipulation:**
  - Provide functionality to shift data between channels, simplifying the analysis and presentation of results.

**RMS Signal Variance Insights:**
  - Address minor discrepancies in RMS signal variance values, attributing them to potential factors like data processing methods, analysis algorithms, and measurement settings.

**Ease of Use and Efficiency:**
  - Enable users to work intuitively and efficiently, making Accel Data Manager an invaluable tool for accelerometry data analysis.

## Technologies

- Java
- Spring Boot
- Spring Data JPA
- Spring Security
- Thymeleaf
- PostgreSQL
- Apache Commons Math

## System Requirements

- Java version 17
- Maven

## Project Structure

The project is organized as follows:

- `src/main/java/io/github/bialekmm/bookingapp/`
  - `controller/`: Controllers handling HTTP requests.
  - `service/`: Business service layer.
  - `repository/`: Database interactions and JPA repositories.
  - `entity/`: Data model entities.
  - `security/`: Spring Security configuration and authentication handling.
- `src/main/resources/`
  - `templates/`: Thymeleaf templates for the user interface.

## Installation and Usage

1. Clone the repository to your local machine.
   ```shell
   git clone https://github.com/bialekmm/AccelDataManager.git
   
2. Navigate to the project directory:
   ```shell
   cd AccelDataManager
   
3. Modify the application.properties file to configure access to the PostgreSQL database.

4. Run the application:
   ```shell
   mvn spring-boot:run

5. Open your web browser and go to http://localhost:1111 to access the application.

## Authors

Maciej Białek - bialeek.m@gmail.com

## License

This project is released under the MIT License.



