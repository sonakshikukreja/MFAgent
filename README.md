# MFAgent - AI-Powered Mutual Funds Intelligence Agent

![Status](https://img.shields.io/badge/Status-Active-brightgreen)
![Java Version](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green)
![License](https://img.shields.io/badge/License-MIT-yellow)

An intelligent AI-powered web application that provides real-time insights about Indian mutual funds by scraping official AMFI (Association of Mutual Funds in India) data and leveraging Google's Gemini AI for comprehensive analysis.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [System Architecture](#system-architecture)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Frontend Usage](#frontend-usage)
- [How It Works](#how-it-works)
- [Key Components](#key-components)
- [Security Considerations](#security-considerations)
- [Troubleshooting](#troubleshooting)
- [Future Enhancements](#future-enhancements)
- [Development](#development)

---

## Overview

**MFAgent** is a Spring Boot-based REST API integrated with a responsive web frontend that answers complex queries about Indian mutual funds. The system works by:

1. **Web Scraping**: Automatically fetches real-time NAV (Net Asset Value) data and fund information from the official AMFI India website
2. **Context Enrichment**: Builds a comprehensive knowledge base from scraped content
3. **AI Analysis**: Uses Google Gemini AI to intelligently analyze queries against the scraped context
4. **User-Friendly Interface**: Provides a clean, modern web interface for interacting with the AI agent

The application is designed for financial advisors, retail investors, and mutual fund enthusiasts who need quick, accurate answers about Indian mutual funds backed by official AMFI data.

---

## Features

- ✅ **Real-Time NAV Data**: Automatically fetches latest NAV data from AMFI's official sources
- ✅ **Intelligent Query Answering**: Uses Google Gemini AI to answer complex questions about mutual funds
- ✅ **Web Scraping**: Recursive crawling of AMFI website to gather comprehensive context
- ✅ **Response Formatting**: Beautiful Markdown-rendered responses with tables and formatted lists
- ✅ **Error Handling**: Graceful error handling for network failures and API issues
- ✅ **Performance Metrics**: Shows processing time for each query
- ✅ **Source Attribution**: Displays sources used to generate responses
- ✅ **Responsive Design**: Mobile-friendly web interface
- ✅ **CORS Support**: Enabled for development and cross-origin requests

---

## System Architecture

### High-Level Flow

```
User Query
    ↓
[Web Frontend] (index.html)
    ↓ HTTP POST
[Spring Boot API] (/query endpoint)
    ↓
[Context Scraper] (Jsoup + HttpClient)
    ↓ HTTPS
[AMFI Website] (www.amfiindia.com)
    ↓ (HTML/Text Data)
[Context Builder]
    ↓
[LLM Caller] (RestTemplate)
    ↓ HTTPS/JSON
[Google Gemini API]
    ↓ (AI Response)
[Response Formatter]
    ↓ JSON
[Web Frontend] (Display Result)
```

### Components

1. **Frontend (index.html)**
   - Single Page Application (SPA) built with vanilla JavaScript
   - Uses Marked.js for Markdown rendering
   - Responsive CSS styling
   - Real-time query submission and result display

2. **Backend (Spring Boot)**
   - REST API with `/query` endpoint
   - Web scraping with Jsoup and Java HttpClient
   - LLM integration with Google Gemini API
   - Configuration management with application.properties

3. **External Services**
   - AMFI India Website (Data Source)
   - Google Gemini API (AI Intelligence)

---

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.2.5
- **Language**: Java 17
- **HTTP Client**: Java HttpClient + Spring RestTemplate
- **Web Scraping**: Jsoup 1.17.2
- **Build Tool**: Maven
- **Server**: Embedded Apache Tomcat

### Frontend
- **HTML5**: Semantic markup
- **CSS3**: Modern styling with CSS variables
- **JavaScript**: Vanilla JS (no frameworks)
- **Markdown Parser**: Marked.js (CDN)
- **HTTP Client**: Fetch API

### External APIs
- **Google Gemini API**: For AI-powered analysis
- **AMFI Website**: Data source for mutual fund information

---

## Project Structure

```
MFAgent/
├── pom.xml                                    # Maven configuration
├── README.md                                  # This file
├── design_diagrams.md                         # Architecture & design diagrams
├── src/
│   ├── main/
│   │   ├── java/in/ai/agent/
│   │   │   ├── AiAgentApplication.java        # Spring Boot entry point
│   │   │   ├── QueryController.java           # REST controller & logic
│   │   │   ├── WebConfig.java                 # Web configuration (CORS, routing)
│   │   │   └── SSLCertificateValidation.java  # SSL handling utility
│   │   └── resources/
│   │       ├── application.properties         # Configuration file
│   │       └── static/
│   │           └── index.html                 # Frontend SPA
│   └── test/                                  # Test files
├── target/                                    # Maven build output
└── [Additional config files as needed]
```

---

## Prerequisites

Before running the application, ensure you have the following installed:

1. **Java Development Kit (JDK) 17** or higher
   - Download from [https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
   - Verify: `java -version`

2. **Maven 3.6+**
   - Download from [https://maven.apache.org/](https://maven.apache.org/)
   - Verify: `mvn -version`

3. **Google Gemini API Key**
   - Create a free account at [https://makersuite.google.com/app/apikey](https://makersuite.google.com/app/apikey)
   - Copy your API key for configuration

4. **Internet Connection**
   - Required for:
     - Scraping AMFI website
     - Calling Google Gemini API
     - Downloading Maven dependencies

5. **Modern Web Browser** (for frontend)
   - Chrome, Firefox, Edge, or Safari (recent versions)

---

## Installation & Setup

### Step 1: Clone or Download the Project

```bash
# If using git
git clone <repository-url>
cd MFAgent

# Or download and extract the project folder
```

### Step 2: Verify Java Installation

```bash
java -version
javac -version
```

### Step 3: Configure API Keys

Edit `src/main/resources/application.properties`:

```properties
# Scraper Configuration (AMFI)
scraper.base-url=https://www.amfiindia.com
scraper.max-depth=10
scraper.max-pages=100

# Gemini API Configuration - REPLACE WITH YOUR API KEY
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent
gemini.api.key=YOUR_GEMINI_API_KEY_HERE  # ← Replace this!

# Server Configuration
server.port=8080
spring.main.web-application-type=servlet
```

### Step 4: Build the Project

```bash
# Clean and build
mvn clean install

# Or just build
mvn package
```

This will:
- Download all dependencies
- Compile Java code
- Run tests
- Package as JAR (if configured)

---

## Configuration

### application.properties Options

```properties
# Server Settings
server.port=8080                              # Port the application listens on
spring.main.web-application-type=servlet      # Use servlet-based web app

# Scraper Configuration
scraper.base-url=https://www.amfiindia.com    # AMFI website base URL
scraper.max-depth=10                          # Maximum recursion depth for scraping
scraper.max-pages=100                         # Maximum number of pages to scrape

# Gemini API Configuration
gemini.api.url=...generateContent             # Gemini API endpoint
gemini.api.key=YOUR_API_KEY                   # Your Gemini API key (REQUIRED)
```

### Adjusting Configuration

- **Scraper Depth/Pages**: Increase `scraper.max-depth` or `scraper.max-pages` for more comprehensive context (but slower response)
- **Server Port**: Change `server.port` if port 8080 is unavailable
- **API Key**: Always keep your Gemini API key secure and never commit it to version control

---

## Running the Application

### Option 1: Run via Maven

```bash
# From the project root directory
mvn spring-boot:run
```

### Option 2: Run the JAR File

```bash
# First, build the project
mvn clean package

# Then run the JAR
java -jar target/MFAgent-1.0-SNAPSHOT.jar
```

### Option 3: Run in IDE

1. Open the project in IntelliJ IDEA or Eclipse
2. Right-click on `AiAgentApplication.java`
3. Select "Run 'AiAgentApplication.main()'"

### Verify the Application is Running

Once started, you should see:

```
WARNING: Global SSL certificate validation has been disabled for HttpsURLConnection. This is for development only.

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| ._ |_| |_|_| |_\__,_| / / / /
 =========|_|====================/_/_/_/
 :: Spring Boot ::                (v3.2.5)

2026-01-15 10:30:45.123  INFO ... - Starting AiAgentApplication
2026-01-15 10:30:47.456  INFO ... - Tomcat started on port(s): 8080
2026-01-15 10:30:47.789  INFO ... - Started AiAgentApplication in 2.456 seconds
```

Access the application at: **http://localhost:8080**

---

## API Endpoints

### POST /query

**Purpose**: Submit a query about mutual funds and receive AI-generated response

**Request**:
```json
{
  "prompt": "What are the top 5 performing equity mutual funds in 2025?"
}
```

**Response**:
```json
{
  "response": "Based on the latest AMFI data...\n\n## Top Performing Funds\n1. Fund A - 18.5% returns\n2. Fund B - 17.2% returns\n...",
  "sources": "https://portal.amfiindia.com/spages/NAVAll.txt, https://www.amfiindia.com/... (and others)",
  "time": "3456 ms"
}
```

**Request Parameters**:
- `prompt` (string, required): The user's query about mutual funds

**Response Fields**:
- `response` (string): The AI-generated answer (Markdown formatted)
- `sources` (string): Comma-separated list of data sources used
- `time` (string): Processing time in milliseconds

**HTTP Status Codes**:
- `200 OK`: Request successful
- `400 Bad Request`: Missing or invalid prompt
- `500 Internal Server Error`: Server error (API key issue, network error, etc.)

**Example cURL Request**:
```bash
curl -X POST http://localhost:8080/query \
  -H "Content-Type: application/json" \
  -d '{"prompt":"What is the current NAV of HDFC Bank MF?"}'
```

### GET / (Frontend)

**Purpose**: Serve the single-page application (index.html)

**Response**: HTML page with embedded CSS and JavaScript

---

## Frontend Usage

### User Interface

The frontend provides a clean, intuitive interface:

1. **Header**: Application title and description
2. **Search Section**: Input field and submit button
3. **Output Area**: Results display area

### How to Use

1. Open http://localhost:8080 in your browser
2. Enter your query in the text field:
   - Examples: "What are the top mutual funds?"
   - "Show NAV data for debt funds"
   - "Which equity funds performed best?"
3. Press **Enter** or click **Ask Agent**
4. Wait for processing (shows "Thinking..." message)
5. View the formatted response with:
   - Main answer (Markdown formatted with tables, lists, headings)
   - Source attribution
   - Processing time

### Features

- **Real-time Processing**: Shows spinner during API call
- **Markdown Rendering**: Responses with tables, lists, and formatting
- **Error Display**: Clear error messages if something goes wrong
- **Responsive Design**: Works on desktop, tablet, and mobile devices
- **Keyboard Support**: Press Enter to submit query

---

## How It Works

### Execution Flow

#### 1. User Submits Query
```
User enters: "What are the top mutual funds?"
Frontend sends: POST /query with JSON body
```

#### 2. Backend Receives Request
```
QueryController.query() method processes the request
Extracts user prompt from request body
Starts timer for performance tracking
```

#### 3. Context Gathering (Parallel Operations)
```
a) Fetch Specific NAV Files:
   - NAVAll.txt (All funds)
   - NAVOpen.txt (Open-ended funds)
   - NAVClose.txt (Close-ended funds)
   - NAVInterval.txt (Interval funds)
   
b) Recursive Website Scraping:
   - Starts from AMFI website base URL
   - Follows internal links (breadth-first)
   - Respects depth and page limits
   - Extracts text content from each page
   - Builds comprehensive context string
   
Context Size: Limited to 30,000 characters to avoid token limits
```

#### 4. Prompt Construction
```
Full Prompt = System Instructions + Context + User Query
System Instructions: "You are an AI specialist in Indian Mutual Funds..."
Context: All scraped data
User Query: Original user question
```

#### 5. LLM Call
```
Sends HTTP POST to Google Gemini API
Payload: JSON with prompt and model name
Receives: JSON response with generated text
Parses response to extract answer
```

#### 6. Response Formatting
```
Builds response object with:
- response: AI-generated answer (string)
- sources: List of URLs used (string)
- time: Processing time in ms (string)

Returns JSON to frontend
```

#### 7. Frontend Display
```
Receives JSON response
Parses Markdown using Marked.js
Renders HTML with formatted styling
Displays to user
```

### Data Flow Diagram

```
User Input
    ↓
JavaScript fetch() call
    ↓
Spring Boot /query endpoint
    ↓ (1) Fetch specific NAV files (HttpClient)
    ↓ (2) Scrape website recursively (Jsoup)
    ↓ (Combine contexts)
    ↓
Build LLM prompt
    ↓
Call Gemini API (RestTemplate)
    ↓
Parse API response
    ↓
Build result JSON
    ↓
JavaScript processes JSON
    ↓
Marked.js renders Markdown
    ↓
Display in browser
```

---

## Key Components

### AiAgentApplication.java

**Purpose**: Spring Boot application entry point and initialization

**Key Features**:
- `@SpringBootApplication`: Enables auto-configuration
- `SSLCertificateValidation.disable()`: Disables SSL validation for development (insecure!)
- `main()`: Starts the Spring Boot application

### QueryController.java

**Purpose**: REST API controller handling all query logic

**Key Methods**:

1. **query(Map<String, String> body)**
   - Handles POST /query requests
   - Orchestrates scraping and LLM calls
   - Returns JSON response

2. **scrapeRecursive(String url, int depth, Set<String> visitedUrls, StringBuilder contextBuilder)**
   - Recursively crawls website starting from URL
   - Respects depth and page count limits
   - Extracts text content from pages
   - Follows internal links only

3. **callGemini(String prompt)**
   - Sends prompt to Google Gemini API
   - Constructs proper JSON payload
   - Handles API response parsing
   - Handles errors and safety blocks

4. **isBinaryFile(String url)**
   - Filters out binary files (PDFs, images, archives)
   - Avoids downloading non-text content

**Configuration Injection**:
```java
@Value("${scraper.base-url}")      // From application.properties
@Value("${scraper.max-depth}")
@Value("${scraper.max-pages}")
@Value("${gemini.api.url}")
@Value("${gemini.api.key}")
```

### WebConfig.java

**Purpose**: Spring Web configuration for routing and CORS

**Features**:
- **addViewControllers()**: Routes "/" to index.html
- **addCorsMappings()**: Enables CORS for all origins (development mode)
- Allows: GET, POST, PUT, DELETE, OPTIONS methods
- Supports credentials

### SSLCertificateValidation.java

**Purpose**: Disables SSL certificate validation for development

**Warning**: ⚠️ **INSECURE - Development Only!**

**What it does**:
- Creates a trust-all SSL context
- Disables hostname verification
- Allows connection to HTTPS endpoints with invalid certificates

**When Used**:
- Called on application startup in `AiAgentApplication`
- Necessary because AMFI website may have certificate issues in some environments

**Production Alternative**:
- Use proper certificate validation
- Configure truststore with valid certificates
- Remove this utility class

### index.html

**Purpose**: Single-page application frontend

**Structure**:
```html
<header>          <!-- Title and description -->
<search-section>  <!-- Input field and button -->
<output>          <!-- Results display area -->
<script>          <!-- JavaScript logic -->
```

**JavaScript Functions**:
- **submitQuery()**: Submits prompt to backend API
- **handleKeyPress()**: Enables Enter key submission
- **fetch()**: Makes HTTP request to /query endpoint
- **marked.parse()**: Converts Markdown to HTML

**Styling**:
- CSS variables for theming (colors, spacing)
- Responsive layout (flexbox)
- Animations (fade-in, spinner)
- Dark mode variables available

---

## Security Considerations

### ⚠️ Important Security Notes

1. **SSL Certificate Validation Disabled**
   - File: `SSLCertificateValidation.java`
   - Issue: Development-only workaround
   - Risk: Vulnerable to man-in-the-middle attacks
   - **Action Required**: Disable in production

2. **API Key Exposure**
   - Gemini API key stored in `application.properties`
   - Risk: Key may be committed to version control
   - **Best Practices**:
     - Use environment variables instead
     - Use Spring Cloud Config
     - Use AWS Secrets Manager / Azure Key Vault
     - Never commit keys to Git

3. **CORS Configuration**
   - `allowedOriginPatterns("*")` allows all origins
   - Suitable for development only
   - **For Production**: Restrict to specific domains

4. **User Input**
   - Prompts are passed directly to LLM
   - Consider adding input validation/sanitization
   - LLM is instructed to only use context data (prevents prompt injection partially)

### Security Recommendations

**Before Production**:
1. ✅ Enable proper SSL certificate validation
2. ✅ Move API keys to environment variables
3. ✅ Restrict CORS to specific domains
4. ✅ Add input validation
5. ✅ Implement rate limiting
6. ✅ Add authentication/authorization
7. ✅ Use HTTPS with valid certificates
8. ✅ Implement logging and monitoring
9. ✅ Add request size limits
10. ✅ Implement timeout handling

### Environment Variables Setup

```bash
# Linux/Mac
export GEMINI_API_KEY="your_key_here"
export AMFI_BASE_URL="https://www.amfiindia.com"
export SERVER_PORT="8080"

# Windows (PowerShell)
$env:GEMINI_API_KEY="your_key_here"
$env:AMFI_BASE_URL="https://www.amfiindia.com"
$env:SERVER_PORT="8080"
```

Modify `application.properties`:
```properties
gemini.api.key=${GEMINI_API_KEY}
scraper.base-url=${AMFI_BASE_URL:https://www.amfiindia.com}
server.port=${SERVER_PORT:8080}
```

---

## Troubleshooting

### Common Issues and Solutions

#### 1. "Connection Refused" Error

**Symptom**: Application starts but API calls fail

**Causes**:
- Internet connection issue
- AMFI website is down
- Network firewall blocking requests

**Solutions**:
```bash
# Check internet connectivity
ping www.amfiindia.com

# Check if AMFI website is accessible
curl https://www.amfiindia.com

# Check port availability
netstat -ano | findstr :8080  # Windows
lsof -i :8080                  # Mac/Linux
```

#### 2. "Gemini API Key Invalid" Error

**Symptom**: Response says "Error calling Gemini API..."

**Causes**:
- API key is incorrect
- API key is not set
- API quota exceeded
- API key doesn't have access to gemini-2.0-flash model

**Solutions**:
```bash
# Verify API key in application.properties
cat src/main/resources/application.properties | grep gemini.api.key

# Test API directly with curl
curl -X POST "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=YOUR_KEY" \
  -H "Content-Type: application/json" \
  -d '{"contents":[{"parts":[{"text":"Hello"}]}]}'

# Check API quotas at https://console.cloud.google.com/apis/dashboard
```

#### 3. "Port 8080 Already in Use"

**Symptom**: 
```
ERROR: Address already in use: bind
```

**Solutions**:
```bash
# Kill process using port 8080 (Windows)
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Kill process using port 8080 (Mac/Linux)
lsof -i :8080
kill -9 <PID>

# Or use a different port in application.properties
server.port=9090
```

#### 4. "SSL Certificate Validation Error"

**Symptom**: 
```
PKIX path building failed
SSLHandshakeException
```

**Current State**: Application disables SSL validation (insecure!)

**Solutions**:
- Application should handle this automatically
- If still failing, check SSL certificate validity:
  ```bash
  openssl s_client -connect www.amfiindia.com:443
  ```

#### 5. "Timeout Waiting for Response"

**Symptom**: Query takes very long or times out

**Causes**:
- Network is slow
- AMFI website is slow
- Scraper depth/pages set too high
- Gemini API is slow

**Solutions**:
```properties
# Reduce scraper scope
scraper.max-depth=5
scraper.max-pages=50

# Increase timeout (in QueryController - currently 5000ms)
.timeout(10000)
```

#### 6. Browser Shows Empty Response

**Symptom**: Click "Ask Agent" but nothing happens

**Causes**:
- Backend server not running
- Frontend JavaScript error
- CORS issue

**Solutions**:
```bash
# Check if server is running
curl http://localhost:8080/

# Check browser console for errors
# Press F12 → Console tab

# Verify CORS headers
curl -X OPTIONS http://localhost:8080/ -v
```

#### 7. "Exception in thread 'main'" at Startup

**Symptom**: Application doesn't start with Java exception

**Causes**:
- Java version mismatch
- Maven dependencies not downloaded
- Corrupted pom.xml

**Solutions**:
```bash
# Verify Java version
java -version  # Should be 17+

# Clean and rebuild
mvn clean install -U

# Check pom.xml for syntax errors
mvn validate

# Delete local Maven cache if necessary
rm -rf ~/.m2/repository
mvn clean install
```

### Debug Mode

Enable debug logging:

**application.properties**:
```properties
logging.level.root=DEBUG
logging.level.in.ai.agent=DEBUG
logging.level.org.springframework.web=DEBUG
```

**Check Logs**:
```bash
# Application console shows detailed logs
# Look for "ERROR", "WARN", "Exception" messages
```

### Performance Optimization

If responses are slow:

1. **Reduce Scraper Scope**:
   ```properties
   scraper.max-depth=3
   scraper.max-pages=25
   ```

2. **Reduce Context Size**:
   Change in QueryController: `30000` to `15000`

3. **Cache Results**:
   Implement caching for frequently asked questions

4. **Increase Timeout**:
   In QueryController: `.timeout(10000)`

---

## Future Enhancements

### Planned Features

1. **Caching Layer**
   - Cache NAV data with TTL
   - Reduce API calls to AMFI
   - Reduce redundant Gemini API calls
   - Implementation: Spring Cache or Redis

2. **Database Integration**
   - Store query history
   - Track user interactions
   - Historical NAV trends
   - Implementation: PostgreSQL + JPA

3. **Advanced Filtering**
   - Filter by fund category, risk level, returns
   - Compare multiple funds
   - Correlation analysis
   - Implementation: Enhanced context processing

4. **Authentication & Authorization**
   - User accounts
   - Personalized recommendations
   - Usage tracking per user
   - Implementation: Spring Security

5. **Portfolio Analysis**
   - Upload portfolio file
   - AI analysis of user's portfolio
   - Recommendations for rebalancing
   - Implementation: File upload + advanced LLM prompts

6. **Real-time Notifications**
   - Alert on NAV changes
   - Market updates
   - Regulatory news
   - Implementation: WebSocket + scheduled tasks

7. **Mobile Apps**
   - Native iOS/Android apps
   - Push notifications
   - Offline mode
   - Implementation: React Native / Flutter

8. **Advanced Analytics**
   - Performance charts
   - Historical trends
   - Risk metrics
   - Implementation: Chart.js / D3.js

9. **Multi-Language Support**
   - Support Hindi, other Indian languages
   - Regional fund information
   - Implementation: i18n framework

10. **Improved Scraping**
    - JavaScript rendering (Selenium/Playwright)
    - Dynamic content handling
    - Rate limiting to respect server load
    - Implementation: Playwright or Selenium

### Technical Debt

- [ ] Replace SSL bypass with proper certificate handling
- [ ] Move API keys to environment variables
- [ ] Add comprehensive unit tests
- [ ] Add integration tests
- [ ] Add API documentation (Swagger/OpenAPI)
- [ ] Implement request logging
- [ ] Add circuit breaker pattern for external APIs
- [ ] Implement rate limiting
- [ ] Add input validation
- [ ] Refactor QueryController (too many responsibilities)

---

## Development

### Building the Project

```bash
# Clean build
mvn clean install

# Build without running tests
mvn clean package -DskipTests

# Build with specific Java version
mvn clean install -Dmaven.compiler.source=17 -Dmaven.compiler.target=17
```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=QueryControllerTest

# Run with coverage
mvn clean test jacoco:report
```

### IDE Setup

#### IntelliJ IDEA
1. File → Open → Select MFAgent folder
2. Mark `src/main/java` as Sources Root
3. Mark `src/main/resources` as Resources Root
4. Set Project SDK to Java 17
5. Run → Edit Configurations → Spring Boot Application

#### VS Code
1. Install extensions: Extension Pack for Java, Spring Boot Extension Pack
2. Open folder in VS Code
3. Command Palette → Java: Create Java Project → Spring Boot
4. Or just open and let extensions auto-configure

#### Eclipse
1. File → Import → Maven → Existing Maven Projects
2. Select MFAgent folder
3. Eclipse will auto-configure Maven project

### Code Structure Guidelines

```
QueryController
├── query() ← Entry point
├── scrapeRecursive() ← Web scraping logic
├── callGemini() ← LLM integration
├── isBinaryFile() ← Utility
└── [Other helper methods]
```

### Adding New Features

Example: Add a new endpoint `/funds-list`

```java
// In QueryController.java
@PostMapping("/funds-list")
public ResponseEntity<?> getFundsList() {
    // Implementation
    return ResponseEntity.ok(...);
}
```

Example: Add new configuration

```properties
# In application.properties
new.feature.enabled=true
new.feature.param=value
```

```java
// In any component
@Value("${new.feature.enabled}")
private boolean featureEnabled;
```

### Git Workflow

```bash
# Create feature branch
git checkout -b feature/new-feature

# Make changes and commit
git add .
git commit -m "Add new feature"

# Push to repository
git push origin feature/new-feature

# Create Pull Request on GitHub
```

### Commit Message Convention

```
[TYPE] Brief description

- More detailed explanation if needed
- Multiple lines allowed

Type: feat, fix, docs, style, refactor, test, chore
```

Examples:
```
feat: Add portfolio analysis endpoint
fix: Handle null response from Gemini API
docs: Update README with troubleshooting section
refactor: Extract scraping logic to separate service
test: Add QueryController integration tests
```

---

## Contributing

We welcome contributions! Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

Please ensure:
- Code follows existing style
- Tests are added for new features
- Documentation is updated
- Commit messages are clear

---

## License

This project is licensed under the MIT License - see LICENSE file for details.

---

## Disclaimer

**MFAgent** is an AI-powered tool and not a substitute for professional financial advice. While it provides information based on official AMFI data, users should:

- Consult with qualified financial advisors before making investment decisions
- Verify information from official sources
- Understand the risks associated with mutual fund investments
- Review fund documents and prospectuses carefully

The developers and contributors are not responsible for any financial decisions made based on information from this application.

---

## Contact & Support

For issues, questions, or suggestions:

- **Open an Issue**: Create a GitHub issue with detailed description
- **Email**: [Your contact email]
- **Documentation**: See design_diagrams.md for architecture details

---

## Acknowledgments

- **Spring Boot Team**: For the excellent framework
- **Google**: For Gemini AI API
- **AMFI India**: For providing official mutual fund data
- **Jsoup**: For web scraping library
- **Marked.js**: For Markdown rendering

---

## Version History

### v1.0.0 (Current)
- ✅ Initial release
- ✅ Basic query functionality
- ✅ Web scraping from AMFI
- ✅ Gemini AI integration
- ✅ Responsive frontend
- ✅ Error handling

### Planned Versions
- v1.1.0: Caching layer
- v1.2.0: Database integration
- v2.0.0: Advanced features (portfolio analysis, etc.)

---

**Last Updated**: January 2026  
**Maintainer**: AI Agent Development Team


