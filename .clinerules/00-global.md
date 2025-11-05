

# Global Project Rules and Standards

## Language and Communication
- All code, comments, and documentation must be written in English
- Use clear and descriptive variable/method names following Java camelCase convention
- Follow Java naming conventions: classes in PascalCase, methods in camelCase
- Constants should be in UPPER_SNAKE_CASE

## Code Quality Standards
- Maintain minimum 80% code coverage with meaningful tests
- Use dependency injection patterns consistently throughout the application
- Implement proper exception handling with custom exceptions where appropriate
- Follow Spring Boot best practices for configuration and component management

## Documentation Requirements
- Update JavaDoc for all public methods and classes
- Maintain comprehensive README.md files for each component
- Document API endpoints using OpenAPI/Swagger annotations
- Keep architecture decision records (ADRs) for significant technical decisions

## Memory Bank Integration
- Update Memory Bank after significant changes or new pattern discovery
- Reference relevant domain files when working with specific services
- Document reusable patterns in systemPatterns.md
- Capture workflow improvements in knowledge-base directory

## Git and Version Control
- Use conventional commit messages with clear, descriptive summaries
- Reference issue numbers in commit messages when applicable
- Maintain clean git history with meaningful commit granularity
- Update CHANGELOG.md for user-facing changes


## Branch name conventions
- Branch name usually starts with a number eg 240912-000071
- All commit messages should start with the issue number in the format 240912-000071 - [Short Description]