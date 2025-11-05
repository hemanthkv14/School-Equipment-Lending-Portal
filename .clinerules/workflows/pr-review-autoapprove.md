

# Pull Request Review and Auto-Approval Workflow

## Purpose
Automates the pull request review process by analyzing code changes, checking quality standards, and providing approval or requesting changes based on configurable criteria.

## Usage Instructions
1. Type: `/pr-review-autoapprove` in Cline chat
2. Provide PR number or URL
3. Configure review criteria (or use defaults)
4. Review automation recommendations before final action

## Workflow Steps

### Phase 1: PR Information Gathering
1. **PR Retrieval**: Fetch PR details using GitHub CLI (`gh pr view <number>`)
2. **Diff Analysis**: Analyze complete diff of changes
3. **Context Collection**: Gather related files and dependencies
4. **Metadata Review**: Check PR title, description, and linked issues

### Phase 2: Quality Assessment
1. **Code Quality Check**: Analyze changes against .clinerules standards
2. **Security Review**: Check for potential security vulnerabilities
3. **Testing Verification**: Ensure appropriate tests are included
4. **Documentation Review**: Verify documentation updates for API changes

### Phase 3: Automated Analysis
1. **Pattern Recognition**: Check for known anti-patterns or code smells
2. **Dependency Impact**: Analyze impact on existing dependencies
3. **Breaking Changes**: Identify potential breaking changes
4. **Performance Impact**: Assess performance implications

### Phase 4: Decision and Action
1. **Risk Assessment**: Evaluate overall risk level of changes
2. **Approval Criteria**: Check against configured approval criteria
3. **Action Recommendation**: Suggest approve, request changes, or manual review
4. **Automated Action**: Execute approved action with detailed comments

## Review Criteria Configuration

### Automatic Approval Conditions
- **Small Changes**: Lines changed < 50 and files changed < 5
- **Documentation Only**: Only markdown or documentation files modified
- **Test Additions**: Only test files added or modified
- **Configuration Updates**: Only configuration files with no logic changes

### Request Changes Conditions
- **Missing Tests**: New functionality without corresponding tests
- **Security Issues**: Potential security vulnerabilities detected
- **Code Quality**: Significant violations of coding standards
- **Breaking Changes**: Changes that break existing API contracts

### Manual Review Required
- **Large Changes**: Lines changed > 200 or files changed > 10
- **Database Migrations**: Changes to database schema
- **Security-Critical**: Changes to authentication or authorization
- **External Dependencies**: Addition or major updates of dependencies

## Integration with Project Standards
- **Coding Standards**: Apply rules from `.clinerules/00-global.md`
- **Component Rules**: Use component-specific rules when applicable
- **Testing Requirements**: Enforce testing standards from concerns/testing.md
- **Security Guidelines**: Apply security rules from concerns/security.md

## Reporting and Documentation
- **Review Summary**: Detailed analysis of changes and decisions
- **Quality Metrics**: Code quality scores and improvement suggestions
- **Risk Assessment**: Clear risk level with justification
- **Action Log**: Record of automated actions taken

## Safety Features
- **Human Override**: Always allow manual override of automated decisions
- **Confirmation Required**: Require confirmation before automated approval
- **Audit Trail**: Maintain complete audit trail of automated reviews
- **Rollback Capability**: Ability to revert automated approvals if needed

## GitHub CLI Integration
```bash
# Fetch PR information
gh pr view <number> --json title,body,headRefName,baseRefName,state,author

# Get PR diff
gh pr diff <number>

# Add review comment
gh pr review <number> --approve --body "Automated approval: meets quality standards"

# Request changes
gh pr review <number> --request-changes --body "Automated review: issues found"
```

## Custom Configuration
- **Team-Specific Rules**: Configure team-specific approval criteria
- **Project Overrides**: Override default settings per project
- **Notification Settings**: Configure team notifications for automated actions
- **Integration Settings**: Configure Slack/Teams notifications for review results