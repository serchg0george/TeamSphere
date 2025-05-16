# Team Sphere

🚀 **Team Sphere** is a Java application designed to manage employees and their tasks in a company.

## 📖 Table of Contents

- [⚙️ Prerequisites](#prerequisites)
- [📥 Installation](#installation)
- [🛠 Configuration](#configuration)
- [▶️ Running the Application](#running-the-application)
- [💻 Frontend Setup](#frontend-setup)
- [📌 API Documentation](#api-documentation)

## ⚙️ Prerequisites

Before you begin, ensure you have met the following requirements:

- **☕ Java Development Kit (JDK):** JDK 21 or higher is required. You can download it from [Oracle](https://www.oracle.com/java/technologies/javase-jdk21-downloads.html) or [OpenJDK](https://openjdk.java.net/projects/jdk/21/).
- **🛠 Maven:** Maven is used for project management and dependency resolution. You can download it from [Apache Maven](https://maven.apache.org/download.cgi).
- **🐘 PostgreSQL:** A PostgreSQL database is required for data persistence. You can download it from [PostgreSQL](https://www.postgresql.org/download/).
- **🌍 Node.js and npm:** Required for the frontend. Download from [nodejs.org](https://nodejs.org/).
- **🖥 IDE (Optional):** An Integrated Development Environment (IDE) like IntelliJ IDEA, Eclipse, or VSCode can be helpful for development.

## 📥 Installation

1. **Clone the repository:**

    ```bash
    git clone <repository_url>
    cd <repository_name>
    ```

2. **Install backend dependencies:**

    ```bash
    mvn clean install
    ```

## 🛠 Configuration

### 🗄 Database and JWT Configuration using application.yml

1. **Database Setup:**

   - Create a PostgreSQL database named `teamsphere` (or your preferred name).

2. **Update application.yml:**

   - Open the file located at `src/main/resources/application.yml` and update the database and JWT settings. For example:

    ```yaml
    spring:
      datasource:
        username: <your_username>
        password: <your_password>
        url: <your_database_connection_url>
      jpa:
        hibernate:
          ddl-auto: update

    jwt:
      secret: <your_secret_key>
      expiration-time: 4d # Example: 4 days
    ```

   - Replace `<your_username>`, `<your_password>`, and `<your_secret_key>` with your actual PostgreSQL credentials and a secure secret key for JWT.

## ▶️ Running the Application

1. **Start the backend:**

    ```bash
    mvn spring-boot:run
    ```

    Alternatively, you can run the application directly from your IDE.

2. **Verify the backend:**

    - Once the backend is running, access it at `http://localhost:8080`.

## 💻 Frontend Setup

1. **Navigate to the frontend directory:**

    (Assuming the frontend project is located in a folder named `frontend`.)

    ```bash
    cd frontend
    ```

2. **Install frontend dependencies:**

    ```bash
    npm install
    ```

3. **Run the development server:**

    ```bash
    npm run dev
    ```

    - The Vite development server will start, and you can access the frontend at the address provided in your terminal ([http://localhost:5173](http://localhost:5173)).

## 📌 API Documentation

- **📑 Swagger UI:**  
  The API documentation is available via Swagger UI. Open your browser and navigate to [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) to view the API endpoints.

- **📂 Postman Collection:**  
  You can find the Postman request collection in the file `TeamSphere.postman_collection.json`.
