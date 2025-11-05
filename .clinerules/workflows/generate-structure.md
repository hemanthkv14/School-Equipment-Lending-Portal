# Advanced File & Business Logic Generation Workflow

## Purpose
Systematically generates new project files, folders, and implements business logic by analyzing reference code structure and your use-case. Seeks clarifications only where reference patterns or requirements are ambiguous.

## Usage Instructions
1. **Invoke**: `/generate-structure` or initiate workflow in Cline/CLI.
2. **Input**:
   - Reference files/folder structure (tree, zip, file list, or code snippets).
   - Description of your new use-case.
3. **Process**:
   - Analyze the reference.
   - Extract not only structure but business logic and integration patterns.
   - Generate new files with implementation, proactively filling in business logic.
   - Ask you for clarifications only where requirements or intent are unclear.
4. **Review**:
   - Receive full markdown output: structure, code, integration, and any clarification points.
   - Approve, edit, or answer queries to finalize.

## Workflow Steps

### Phase 1: Reference & Requirement Intake
- Provide reference folder/file(s) and brief for new use-case.
- Specify any special framework, config, or integration requirements.

### Phase 2: Structure & Logic Extraction
- Parse reference: file types, folder layout, class/service patterns, integration details.
- Extract sample business logic (service calls, API usages, transformations, etc.).

### Phase 3: New Structure & Implementation Plan
- Propose new file/folder structure based on reference and your use-case.
- Draft method/class/interface names and responsibilities.
- If business requirements or data contracts are unclear, ask pointed questions.

### Phase 4: Code & Logic Generation
- Generate each new file with as much concrete logic as possible:
    - Use best practices and common design patterns.
    - Implement external calls, data parsing, error handling, etc., if intent is unambiguous.
    - Add minimal TODOs if something is explicitly out of scope or undefined.
- Insert clarification questions as comments or markdown checklist items where needed.

### Phase 5: Output & Review
- Present:
    - File/folder tree.
    - Full code for each new file (high-quality, reviewed for logic and integration).
    - List of any assumptions and points needing your clarification.
- Revise as needed based on your feedback.

### Quality and Documentation
- All output is concise, clear, and formatted for quick onboarding.
- Steps, logic, and decisions are documented in markdown for reusability and team sharing.

---

## Example Output

### File Tree
File Tree
text
src/main/java/com/example/customer/
    CustomerDataFetcher.java
    CustomerDataFetcherService.java
    CustomerDataFetcherController.java
src/main/resources/
    application-customerdata.yml
src/test/java/com/example/customer/
    CustomerDataFetcherTest.java
Business Logic Example (with Clarification Points)
Files updated ... eg CustomerDataFetcher.java

#### [Clarification Needed]
- Do you want error handling via exception or return null?
- Should logging be via SLF4J, Log4J, or plain System.out?
- Is auth always "Bearer", or another scheme?
- Is the customer API response always flat, or nested/complex?

---

## Next Steps

- **You provide reference + new use-case**.
- **I generate everything (structure, logic, docs)** and ask questions inline if anything is ambiguous.
- **You confirm or clarify—workflow is complete!**

---

> ### Ready to try?
> Upload or paste your reference structure and describe your next use-case.  
> I’ll handle the rest, and will only ask you for business clarifications where absolutely necessary!
