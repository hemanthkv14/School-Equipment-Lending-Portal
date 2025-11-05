

# Release and Changelog Generation Workflow

## Purpose
Automates the release process by analyzing merged pull requests, generating comprehensive changelogs, updating version numbers, and creating release tags.

## Usage Instructions
1. Type: `/release-changelog` in Cline chat
2. Specify release type (major, minor, patch) or let system detect
3. Configure changelog sections and formatting
4. Review generated changelog before finalizing release

## Workflow Steps

### Phase 1: Release Preparation
1. **Current State Analysis**: Analyze current branch state and version
2. **Merged PR Collection**: Gather all merged PRs since last release
3. **Version Calculation**: Determine next version based on changes
4. **Release Planning**: Plan release timeline and components

### Phase 2: Changelog Generation
1. **PR Categorization**: Categorize PRs by type (feature, bugfix, breaking)
2. **Impact Analysis**: Assess impact of changes on users and developers
3. **Content Generation**: Generate detailed changelog entries
4. **Format Application**: Apply consistent formatting and structure

### Phase 3: Version Management
1. **Version Update**: Update version in pom.xml and other relevant files
2. **Dependency Updates**: Update internal dependency versions
3. **Documentation Updates**: Update version references in documentation
4. **Build Verification**: Verify build works with new version

### Phase 4: Release Creation
1. **Git Tag Creation**: Create annotated git tag for release
2. **Release Branch**: Create release branch if using GitFlow
3. **Build Artifacts**: Generate release artifacts and distributions
4. **Release Publishing**: Publish release to appropriate repositories

## Version Detection Rules
- **Major Version**: Breaking changes, major feature additions
- **Minor Version**: New features, significant improvements
- **Patch Version**: Bug fixes, minor improvements, security patches
- **Semantic Versioning**: Follow semver.org principles

## Changelog Structure
```markdown
# Changelog

## [X.Y.Z] - YYYY-MM-DD

### Added
- New features and capabilities

### Changed
- Changes in existing functionality

### Deprecated
- Soon-to-be removed features

### Removed
- Now removed features

### Fixed
- Bug fixes

### Security
- Security vulnerability fixes
```

## PR Analysis Patterns
- **Conventional Commits**: Parse conventional commit messages
- **Label-Based**: Use GitHub labels for categorization
- **Keyword Detection**: Detect breaking changes, features, fixes
- **Impact Assessment**: Analyze affected components and users

## Integration Points
- **Maven Version**: Update version in pom.xml files
- **Documentation**: Update API documentation versions
- **Docker Tags**: Update Docker image tags
- **Deployment Scripts**: Update deployment configurations

## Automation Features
- **Auto-Detection**: Automatically detect release type from changes
- **Template Support**: Use customizable changelog templates
- **Multi-Component**: Handle multi-component releases
- **Rollback Support**: Ability to rollback release if issues found

## Quality Checks
- **Build Verification**: Ensure code builds successfully
- **Test Execution**: Run full test suite before release
- **Security Scan**: Perform security scan of release artifacts
- **Documentation Verification**: Ensure documentation is up to date

## GitHub Integration
```bash
# Get merged PRs since last release
gh pr list --state merged --base main --limit 100

# Create release tag
git tag -a v1.2.3 -m "Release version 1.2.3"

# Push tag to remote
git push origin v1.2.3

# Create GitHub release
gh release create v1.2.3 --title "Release 1.2.3" --notes-file CHANGELOG.md
```

## Notification and Communication
- **Team Notifications**: Notify team of release completion
- **Stakeholder Updates**: Send release notes to stakeholders
- **Documentation Updates**: Update public documentation
- **Deployment Coordination**: Coordinate with deployment teams