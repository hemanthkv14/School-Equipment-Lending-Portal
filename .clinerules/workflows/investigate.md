# Deep Code Investigation & Flow Mapping Command

## Purpose
Thoroughly investigates any code element, feature, or question (e.g. endpoint, method, data flow, communication pattern), revealing all relevant details, logic, and dependencies. Recursively traces control flow, method calls, and communications. Outputs a detailed, well-structured markdown with stepwise explanation and Mermaid diagrams showing the entire trace and interactions.

---

## Usage Instructions

1. **Invoke the Command**:  
   `/investigate [target]`  
   Example:  
   - `/investigate POST /api/customer/info`
   - `/investigate How does data sync work?`
   - `/investigate What triggers CustomerNotificationService?`
2. **(Optional) Provide Context**:  
   - File or code location if known (e.g., controller, file, or method name).

3. **Investigation Process**:
   1. Identify the exact location (file, method, class) where the target is defined or handled.
   2. Recursively trace all downstream and upstream flows:
      - For endpoints: route → controller → service → repository → external calls (etc).
      - For internal logic: method calls, event triggers, listeners, background jobs.
      - For communication: network calls, message queues, or inter-service APIs.
   3. At each step:
      - Describe what happens (purpose, params, major logic).
      - Note key configs, environment variables, or integrations.
      - Drill down until all dependencies, data flows, and side effects are mapped.
   4. Generate one or more **mermaid diagrams** that visualize the flow or structure.

---

## Workflow Steps

### 1. Target Identification
- Accept the investigation target (endpoint, method, job, or abstract question).
- Locate entry point(s) in the codebase.

### 2. Recursive Deep-Dive
For each step in the call chain or flow:
- **Describe** what the function/class/module does.
- **List** key parameters, input/output, and external dependencies.
- **Trace** all outgoing calls or triggers:
  - If calling another function/service, recursively follow it.
  - If persisting/retrieving data, show models and queries.
  - If emitting events or messages, trace consumers/listeners.

### 3. Integration and Configuration Mapping
- Document how external config, environment variables, or feature flags influence this flow.
- Note any integration points: third-party APIs, services, or databases.

### 4. Visual Flow Construction
- Build one or more **mermaid diagrams**:
  - **Sequence Diagram** for call flows (recommended for endpoints).
  - **Flowchart** for logic or data processing.
  - **Class Diagram** for static structures.

### 5. Evidence & Source Linking
- Quote relevant code snippets at each step.
- Reference files, functions, or docs for context.

---

## Output Format

- **Investigation Target**: `<user input>`
- **Stepwise Explanation**: Deep breakdown, each step drilled into recursively.
- **Mermaid Diagrams**: One or more, placed in context.
- **Summary Table** (optional): Key flows, configs, and integrations.
- **Open Questions** (if any): For any ambiguous or external aspects.

---

---

### Summary Table

| Layer         | File/Class               | Method                  | Purpose                            |
|---------------|--------------------------|-------------------------|------------------------------------|
| Controller    | CustomerController.java  | getCustomerInfo         | Handles API request                |
| Service       | CustomerService.java     | fetchCustomerInfo       | Business logic                     |
| Repository    | CustomerRepository.java  | findById                | DB query for customer by ID        |
| Database      | Customers Table          | -                       | Stores customer records            |

---

### Open Questions/Areas for Further Investigation

- Are there interceptors, filters, or security involved in this flow?
- Is data validation or transformation handled elsewhere?
- Is any event emitted after customer info is fetched?

---

## Best Practices

- **Always** recurse until all leaf dependencies are mapped, even external calls or DB queries.
- **Always** visualize the flow, not just describe it.
- **Always** quote code and reference source files.
- **Always** raise further questions if something is unclear.

---

## Ready to Use

- Just use `/investigate [target]`
- Provide more context if needed
- Review the deep breakdown, diagrams, and summary in the generated markdown.

