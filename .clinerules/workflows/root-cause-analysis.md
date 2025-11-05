
# Advanced Root Cause Analysis Workflow

## Purpose
Systematically investigates issues by analyzing affected files, commit history, and related changes to identify root causes using automated file discovery and git analysis.

## Usage Instructions
1. Type: `/root-cause-analysis` in Cline chat
2. Provide issue description, error messages, and timeline
3. Let workflow guide systematic investigation
4. Review findings and validate conclusions

## Workflow Steps

### Phase 1: Issue Intake and Context Gathering
1. **Problem Definition**: Analyze issue description and symptoms
2. **Timeline Establishment**: Determine when issue started occurring
3. **Initial Evidence**: Collect error messages, logs, and user reports
4. **Scope Assessment**: Determine affected systems and components

### Phase 2: Automated File Discovery
1. **Error Message Analysis**: Extract file paths and error keywords from messages
2. **Keyword Search**: Search codebase for relevant terms using `search_files`
3. **File Mention Collection**: Use `@/path/to/file` for each candidate file
4. **Dependency Mapping**: Identify related files through imports and references

### Phase 3: Git History Investigation
1. **Recent Changes Analysis**: Use `@git-changes` to analyze current uncommitted state
2. **Commit Timeline**: Examine commits around the issue occurrence time
3. **Blame Analysis**: Use git blame to identify recent line changes in affected files
4. **Author and Pattern Analysis**: Identify patterns in recent changes

### Phase 4: Systematic Investigation (Five Whys)
1. **First Why**: Why did the specific error occur?
   - Analyze immediate cause from error messages
   - Examine the exact line or component that failed
2. **Second Why**: Why did that component fail?
   - Look at recent changes to the failing component
   - Check configuration and environment changes
3. **Third Why**: Why were those changes made?
   - Examine commit messages and PR descriptions
   - Understand the intent behind the changes
4. **Fourth Why**: Why wasn't the issue caught earlier?
   - Review testing coverage and CI/CD processes
   - Analyze review processes for the changes
5. **Fifth Why**: Why do these process gaps exist?
   - Examine team processes and knowledge gaps
   - Identify systemic issues that allowed the problem

### Phase 5: Evidence Collection and Validation
1. **Change Timeline Construction**: Build chronological view of relevant changes
2. **Impact Analysis**: Assess scope of affected functionality
3. **Hypothesis Testing**: Test theories by examining evidence
4. **Root Cause Confirmation**: Validate most likely cause with supporting evidence

### Phase 6: Solution Development and Documentation
1. **Immediate Fix**: Develop solution for the current issue
2. **Prevention Strategy**: Create plan to prevent similar issues
3. **Process Improvements**: Recommend process improvements
4. **Knowledge Capture**: Document findings in Memory Bank

## Git Analysis Commands Used

### Current State Analysis
```bash
# Check current working state
@git-changes

# This captures git status and git diff output
```

### Commit History Analysis
```bash
# Analyze specific commits
@[commit-hash]

# Recent commits for specific files
git log --oneline -10 path/to/file.java | cat

# Commits in time range
git log --since="2025-06-01" --until="2025-06-18" | cat

# Find files changed together
git show --name-only [commit-hash] | cat
```

### Blame and Line-Level Analysis
```bash
# Basic blame analysis
git blame path/to/file.java

# Blame with date range
git blame --since="2 weeks ago" path/to/file.java | cat

# Blame specific line ranges
git blame -L 100,200 path/to/file.java | cat
```

## Automated Pattern Recognition
1. **Timing Correlation**: Match issue timing with commit timestamps
2. **Change Clustering**: Identify files frequently changed together
3. **Author Patterns**: Analyze if issues correlate with specific authors
4. **Merge Conflicts**: Detect problematic merge commits

## Example Investigation Flow

### Sample Issue
"Users reporting authentication failures since yesterday. Error: 'Invalid token signature'"

### Automated Analysis Steps
1. **File Discovery**: Search for "token", "signature", "auth" keywords
2. **File Analysis**: Examine `@/src/auth/TokenValidator.java`, `@/src/auth/AuthService.java`
3. **Git History**: Check `@git-changes` and recent commits with `@[commit-hash]`
4. **Timeline**: Build timeline of auth-related changes in past 7 days
5. **Root Cause**: Identify token algorithm change in recent deployment

## Integration with Project Components
- **Memory Bank**: Reference security-services.md for authentication patterns
- **Component Rules**: Apply auth component rules for investigation
- **Knowledge Base**: Save investigation patterns for future reference

## Output and Reporting
1. **Investigation Timeline**: Chronological view of events and changes
2. **Root Cause Summary**: Clear identification of the underlying cause
3. **Evidence Collection**: All supporting evidence and analysis
4. **Recommendations**: Immediate fixes and long-term improvements
5. **Process Lessons**: What could be improved to prevent similar issues

## Quality Assurance
- **Evidence-Based**: All conclusions supported by concrete evidence
- **Systematic Approach**: Follow structured investigation methodology
- **Documentation**: Comprehensive documentation of investigation process
- **Validation**: Cross-check findings with multiple sources of evidence