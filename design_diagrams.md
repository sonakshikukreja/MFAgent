# MFAgent Design Documentation

This document contains architectural and design diagrams for the MFAgent application, generated using Mermaid.js.

## 1. System Context Diagram (C4 Level 1)
High-level view of how the User interacts with the MFAgent and external systems.

```mermaid
C4Context
    title System Context Diagram for MFAgent
    Person(user, "User", "A person interested in Indian Mutual Funds")
    System(mfagent, "MFAgent System", "AI-powered agent that answers queries about Mutual Funds")
    System_Ext(amfi, "AMFI Website", "Official Association of Mutual Funds in India website")
    System_Ext(ollama, "Ollama LLM", "Local/Remote LLM API (Llama3)")

    Rel(user, mfagent, "Asks queries", "HTTPS")
    Rel(mfagent, amfi, "Fetches Context/NAV", "HTTPS/HTML/Text")
    Rel(mfagent, ollama, "Generates Answers", "JSON/REST")
```

## 2. Container Diagram (C4 Level 2)
Shows the high-level technical building blocks.

```mermaid
C4Container
    title Container Diagram for MFAgent
    Person(user, "User", "End user")
    
    Container_Boundary(c1, "MFAgent Application") {
        Container(web_app, "Single Page Application", "HTML/JS", "Provides UI for query input and display")
        Container(api_app, "API Application", "Java/Spring Boot", "Handles requests, orchestrates context fetching and LLM calls")
    }

    System_Ext(amfi, "AMFI Website", "Source of Truth")
    System_Ext(ollama, "Ollama API", "Inference Engine")

    Rel(user, web_app, "Uses", "Browser")
    Rel(web_app, api_app, "Submits Query", "JSON/HTTPS")
    Rel(api_app, amfi, "Scrapes/Fetches", "Jsoup/HttpClient")
    Rel(api_app, ollama, "Prompts", "RestTemplate")
```

## 3. Component Diagram (C4 Level 3)
Details the internal components of the Spring Boot API.

```mermaid
C4Component
    title Component Diagram - API Application
    
    Container(spa, "SPA", "HTML/JS", "Frontend")
    
    Container_Boundary(api, "Spring Boot Backend") {
        Component(controller, "QueryController", "Spring MVC RestController", "Exposes /query endpoint")
        Component(fetcher, "Context Fetcher", "Internal Method", "Fetches data from AMFI (Jsoup/HttpClient)")
        Component(llm_client, "LLM Client", "Internal Method", "Communicates with Ollama (RestTemplate)")
        Component(config, "WebConfig", "Configuration", "Maps static resources")
    }

    Rel(spa, controller, "POST /query")
    Rel(controller, fetcher, "Invokes")
    Rel(controller, llm_client, "Invokes")
```

## 4. Deployment Diagram
Shows how the system is deployed physically/virtually.

```mermaid
graph TD
    subgraph User_Device [User Device]
        Browser[Web Browser]
    end

    subgraph Server_Node [Application Server]
        Tomcat[Embedded Tomcat]
        SpringBoot[MFAgent Jar]
    end

    subgraph External_Services [External Cloud/Network]
        AMFI[AMFI Web Server]
        OllamaServer[Ollama Server (GPU/CPU)]
    end

    Browser -- HTTP/8080 --> Tomcat
    Tomcat -- Hosts --> SpringBoot
    SpringBoot -- HTTPS --> AMFI
    SpringBoot -- HTTP/11434 --> OllamaServer
```

## 5. Use Case Diagram
Functional requirements and actors.

```mermaid
usecaseDiagram
    actor User
    actor "AMFI System" as AMFI
    actor "Ollama AI" as AI

    package MFAgent {
        usecase "Submit Query" as UC1
        usecase "View Response" as UC2
        usecase "Fetch Market Data" as UC3
        usecase "Generate Insight" as UC4
        usecase "Handle Errors" as UC5
    }

    User --> UC1
    User --> UC2
    UC1 ..> UC3 : include
    UC1 ..> UC4 : include
    UC3 --> AMFI
    UC4 --> AI
    UC1 --> UC5 : extends
```

## 6. Class Diagram
Structure of the Java classes.

```mermaid
classDiagram
    class AiAgentApplication {
        +main(String[] args)
    }
    
    class WebConfig {
        +addViewControllers(ViewControllerRegistry registry)
    }

    class QueryController {
        +query(Map<String, String> body) Map<String, Object>
        -fetchContext() String
        -callOllama(String prompt) String
    }

    AiAgentApplication ..> QueryController : Component Scan
    AiAgentApplication ..> WebConfig : Component Scan
    QueryController ..> RestTemplate : Uses
    QueryController ..> HttpClient : Uses
    QueryController ..> Jsoup : Uses
```

## 7. Sequence Diagram: Happy Path
Successful query execution flow.

```mermaid
sequenceDiagram
    autonumber
    actor User
    participant UI as Index.html
    participant QC as QueryController
    participant AMFI as AMFI Website
    participant LLM as Ollama API

    User->>UI: Enter prompt & Click Submit
    UI->>QC: POST /query {prompt}
    activate QC
    
    par Fetch Context
        QC->>AMFI: GET / (Home Page)
        AMFI-->>QC: HTML Content
    and Fetch NAV
        QC->>AMFI: GET /spages/NAVAll.txt
        AMFI-->>QC: Text Data
    end

    QC->>QC: Construct Full Prompt (Context + User Query)
    
    QC->>LLM: POST /api/generate {model, prompt}
    activate LLM
    LLM-->>QC: JSON Response {response: "..."}
    deactivate LLM

    QC-->>UI: JSON {response, sources, time}
    deactivate QC
    
    UI->>User: Display Answer & Sources
```

## 8. Sequence Diagram: Context Fetch Failure
Handling errors when AMFI is down.

```mermaid
sequenceDiagram
    participant QC as QueryController
    participant AMFI as AMFI Website

    QC->>QC: fetchContext()
    
    alt Fetch Home Page
        QC->>AMFI: GET /
        AMFI-->>QC: Exception (Timeout/404)
        QC->>QC: Append "Error fetching home page"
    end

    alt Fetch NAV
        QC->>AMFI: GET /NAVAll.txt
        AMFI-->>QC: Exception (Connection Refused)
        QC->>QC: Append "Error fetching NAV data"
    end
    
    QC-->>QC: Return partial/error context
```

## 9. Sequence Diagram: LLM Failure
Handling errors when Ollama is unreachable.

```mermaid
sequenceDiagram
    participant QC as QueryController
    participant LLM as Ollama API

    QC->>LLM: POST /api/generate
    
    alt Connection Error
        LLM--xQC: Connection Refused
        QC-->>QC: Catch Exception
        QC-->>QC: Return "Error calling Ollama API..."
    else Empty Response
        LLM-->>QC: null body
        QC-->>QC: Return "Error... Empty response"
    end
```

## 10. Activity Diagram: User Interaction
Flow of user actions on the frontend.

```mermaid
flowchart TD
    Start((Start)) --> Input[User enters query]
    Input --> Submit{Click Submit?}
    Submit -- No --> Input
    Submit -- Yes --> Loading[Show 'Processing...' UI]
    Loading --> CallAPI[Call Backend API]
    CallAPI --> CheckStatus{Response OK?}
    
    CheckStatus -- Yes --> ParseJSON[Parse JSON]
    ParseJSON --> Display[Display Response, Sources, Time]
    
    CheckStatus -- No --> ParseText[Parse Error Text]
    ParseText --> ShowError[Display Error Message]
    
    Display --> End((End))
    ShowError --> End
```

## 11. Activity Diagram: Backend Processing
Logic flow inside `QueryController`.

```mermaid
flowchart TD
    Start([Start Request]) --> Extract[Extract User Prompt]
    Extract --> Timer[Start Timer]
    
    subgraph Context Fetching
        FetchHome[Jsoup Connect Home] --> |Success/Fail| AppendHome[Append to Context]
        FetchNAV[HttpClient Get NAV] --> |Success/Fail| AppendNAV[Append to Context]
    end
    
    AppendHome --> Merge[Merge Context]
    AppendNAV --> Merge
    
    Merge --> BuildPrompt[Construct LLM Prompt]
    BuildPrompt --> CallLLM[POST to Ollama]
    
    CallLLM --> Success{Success?}
    Success -- Yes --> ExtractResp[Extract Answer]
    Success -- No --> ErrorResp[Set Error Message]
    
    ExtractResp --> StopTimer[Stop Timer]
    ErrorResp --> StopTimer
    
    StopTimer --> BuildResult[Build Result Map]
    BuildResult --> Return([Return JSON])
```

## 12. State Transition Diagram: Request Lifecycle
Conceptual states of a request within the controller.

```mermaid
stateDiagram-v2
    [*] --> Received
    Received --> FetchingContext : Start Processing
    FetchingContext --> ContextReady : Data Retrieved (or Failed handled)
    ContextReady --> QueryingLLM : Prompt Constructed
    QueryingLLM --> ResponseReceived : LLM Responded
    QueryingLLM --> Failed : LLM Error
    ResponseReceived --> Completed : Result Built
    Failed --> Completed : Error Result Built
    Completed --> [*]
```

## 13. State Transition Diagram: UI Component
States of the frontend interface.

```mermaid
stateDiagram-v2
    [*] --> Idle
    Idle --> Inputting : User types
    Inputting --> Submitting : Click Submit
    Submitting --> Loading : API Request Sent
    Loading --> Success : 200 OK
    Loading --> Error : 4xx/5xx Error
    Success --> Idle : New Query
    Error --> Idle : Retry
```

## 14. Entity Relationship Diagram (Conceptual)
Data model of the information flow.

```mermaid
erDiagram
    USER ||--o{ QUERY : submits
    QUERY ||--|| CONTEXT : enriched_with
    CONTEXT ||--|{ SOURCE : contains
    QUERY ||--|| RESPONSE : generates
    RESPONSE }|--|| LLM_MODEL : produced_by

    QUERY {
        string prompt
        timestamp created_at
    }
    CONTEXT {
        string raw_text
        string nav_data
    }
    RESPONSE {
        string answer
        long processing_time
        string sources
    }
```

## 15. Data Flow Diagram (Level 0)
High-level data movement.

```mermaid
flowchart LR
    User[User] -- Prompt --> System(MFAgent)
    System -- Query --> AMFI(AMFI Website)
    AMFI -- HTML/Text --> System
    System -- Prompt+Context --> Ollama(Ollama LLM)
    Ollama -- Answer --> System
    System -- Response --> User
```

## 16. Data Flow Diagram (Level 1)
Detailed data movement within the system.

```mermaid
flowchart TD
    User[User] --> |Prompt| Controller[QueryController]
    
    Controller --> |URL| Jsoup[Jsoup Lib]
    Jsoup --> |HTTP GET| AMFI_Web[AMFI Home]
    AMFI_Web --> |HTML| Jsoup
    Jsoup --> |Parsed Text| Controller
    
    Controller --> |URI| Http[HttpClient]
    Http --> |HTTP GET| AMFI_NAV[AMFI NAV File]
    AMFI_NAV --> |Text| Http
    Http --> |String Body| Controller
    
    Controller --> |Combined Prompt| Rest[RestTemplate]
    Rest --> |JSON Payload| Ollama[Ollama API]
    Ollama --> |JSON Response| Rest
    Rest --> |Answer String| Controller
    
    Controller --> |JSON Result| User
```

## 17. Mind Map: Project Structure
Visualizing the project organization.

```mermaid
mindmap
  root((MFAgent))
    src
      main
        java
          in.ai.agent
            AiAgentApplication
            QueryController
            WebConfig
        resources
          static
            index.html
          application.properties
    pom.xml
    Dependencies
      Spring Web
      Jsoup
      Spring Test
```

## 18. Gantt Chart: Implementation Plan
Hypothetical timeline for building this agent.

```mermaid
gantt
    title MFAgent Development Timeline
    dateFormat  YYYY-MM-DD
    section Setup
    Project Init        :done,    des1, 2024-05-01, 1d
    Dependencies        :done,    des2, 2024-05-01, 1d
    section Backend
    Controller Logic    :active,  des3, 2024-05-02, 2d
    AMFI Integration    :active,  des4, 2024-05-03, 1d
    Ollama Integration  :         des5, 2024-05-04, 1d
    section Frontend
    HTML/JS UI          :         des6, 2024-05-05, 1d
    Error Handling      :         des7, 2024-05-06, 1d
    section Testing
    Integration Test    :         des8, 2024-05-07, 1d
```

## 19. Git Graph
Hypothetical version control history.

```mermaid
gitGraph
    commit id: "init"
    commit id: "setup-pom"
    branch feature/backend
    checkout feature/backend
    commit id: "add-controller"
    commit id: "integrate-jsoup"
    checkout main
    merge feature/backend
    branch feature/frontend
    checkout feature/frontend
    commit id: "create-index"
    commit id: "add-fetch-logic"
    checkout main
    merge feature/frontend
    commit id: "fix-error-handling"
```

## 20. Requirement Diagram
Mapping functional requirements to system components.

```mermaid
requirementDiagram

    requirement req1 {
        id: 1
        text: "User shall be able to input queries"
        risk: low
        verifymethod: test
    }

    requirement req2 {
        id: 2
        text: "System shall fetch real-time context from AMFI"
        risk: medium
        verifymethod: inspection
    }

    requirement req3 {
        id: 3
        text: "System shall use LLM for answer generation"
        risk: high
        verifymethod: demonstration
    }

    element index_html {
        type: file
    }

    element QueryController {
        type: class
    }

    index_html - satisfies -> req1
    QueryController - satisfies -> req2
    QueryController - satisfies -> req3
```

## 21. Pie Chart: Code Composition
Rough estimation of code distribution.

```mermaid
pie title Code Composition
    "Java (Backend Logic)" : 60
    "HTML/JS (Frontend)" : 20
    "XML/Config (Maven/Props)" : 10
    "Documentation" : 10
```
