
# Commit-Based Workflow and Knowledge Generator

## Purpose
Analyzes specific commits to extract methodologies, create reusable workflows, and generate knowledge documentation for future reference and team sharing.

## Usage Instructions
1. Type: `/commit-to-workflow` in Cline chat
2. Provide commit hash(es): `@[commit-hash]` or multiple commits
3. Specify type of workflow/documentation needed
4. Review and refine generated content before saving

## Workflow Steps

### Phase 1: Commit Analysis and Context Extraction
1. **Commit Information Retrieval**: Extract complete commit details using `@[commit-hash]`
2. **Change Analysis**: Analyze the diff to understand what was changed
3. **Context Understanding**: Interpret commit message and purpose
4. **Pattern Recognition**: Identify repeatable processes and methodologies

### Phase 2: Methodology Extraction
1. **Step Identification**: Break down changes into logical steps
2. **Decision Points**: Identify key decisions made during implementation
3. **Tool Usage**: Document tools and commands used
4. **Best Practices**: Extract best practices demonstrated in the commit

### Phase 3: Workflow Generation
1. **Template Creation**: Create structured workflow template
2. **Step Documentation**: Document each step with clear instructions
3. **Parameter Identification**: Identify configurable parameters
4. **Example Integration**: Include examples from the original commit

### Phase 4: Knowledge Documentation
1. **Comprehensive Documentation**: Create detailed reference documentation
2. **Cross-References**: Link to related commits and workflows
3. **Indexing**: Add to master knowledge index
4. **Team Sharing**: Prepare for team-wide distribution

## Supported Commit Types

### Bug Fix Commits
- **Problem Identification**: Document how the bug was identified
- **Investigation Process**: Capture debugging methodology
- **Solution Implementation**: Document fix approach and reasoning
- **Testing Strategy**: Extract testing approach used

### Feature Implementation Commits
- **Design Decisions**: Capture architectural and design choices
- **Implementation Pattern**: Document coding patterns and approaches
- **Integration Strategy**: How the feature integrates with existing code
- **Configuration Changes**: Document configuration updates needed

### Refactoring Commits
- **Refactoring Strategy**: Document approach to code reorganization
- **Risk Mitigation**: How refactoring risks were managed
- **Testing Approach**: Ensuring functionality preservation
- **Performance Impact**: Analysis of performance implications

### Configuration and DevOps Commits
- **Environment Setup**: Document environment configuration steps
- **Deployment Process**: Capture deployment methodology
- **Monitoring Setup**: Document monitoring and alerting configuration
- **Rollback Procedures**: Document rollback capabilities

## Generated Workflow Structure

### Standard Workflow Template
```markdown
# [Workflow Name] - Generated from Commit @[hash]

## When to Use
- Specific scenarios where this workflow applies
- Conditions that trigger this workflow

## Prerequisites
- Required tools and setup
- Permissions and access needed

## Steps (Based on Commit @[hash])
1. **Step Name**: Detailed description
   - Commands to execute
   - Expected outcomes
   - Troubleshooting notes

2. **Next Step**: Continue pattern...

## Reference Information
- **Original Commit**: @[commit-hash]
- **Author**: [Developer Name]
- **Date**: [Commit Date]
- **Related Issues**: [Issue numbers if available]

## Success Criteria
- How to validate successful completion
- Expected results and outcomes

## Common Issues and Solutions
- Known problems and their solutions
- Troubleshooting guide