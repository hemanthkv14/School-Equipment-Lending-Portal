

# Dynamic XML i18n Label Extraction Workflow

## Purpose
Dynamically extracts text labels from any selected XML file, converts them to constant format (CONFIG_XYZ), and updates i18n files with proper organization and sorting.

## Usage Instructions
1. Type: `/xml-i18n-extract` in Cline chat
2. Select XML file using: `@/path/to/your/xml/file.xml`
3. Specify target msgbases files (or use defaults)
4. Review and approve each processing step

## Workflow Steps

### Phase 1: File Analysis and Preparation
1. **File Validation**: Verify XML file structure and accessibility
2. **Backup Creation**: Create timestamped backups of target msgbases files
3. **Content Analysis**: Analyze XML structure and identify label patterns
4. **Processing Plan**: Generate plan for label extraction and conversion

### Phase 2: Label Extraction and Conversion
1. **Pattern Recognition**: Extract labels from XML elements and attributes
2. **Label Conversion**: Transform to constant format using these rules:
   - Remove special characters and spaces
   - Convert to uppercase
   - Use underscores for word separation
   - Apply appropriate prefixes (CONFIG_, UI_, ERROR_, VALIDATION_)
3. **Duplicate Detection**: Check for existing labels to prevent conflicts
4. **Quality Validation**: Ensure converted labels meet naming standards

### Phase 3: File Updates and Organization
1. **msgbases.txt Update**: Add new labels with descriptions
2. **Master msgbases Update**: Add label constants to master file
3. **Additional Files**: Update any additional i18n files specified
4. **Alphabetical Sorting**: Sort all target files alphabetically
5. **Validation Check**: Verify file integrity and format consistency

### Phase 4: Reporting and Documentation
1. **Processing Summary**: Generate detailed report of changes made
2. **Statistics**: Provide counts of labels extracted, added, and updated
3. **Pattern Documentation**: Document any new patterns discovered
4. **Memory Bank Update**: Update relevant memory bank files with findings

## Example Processing

### Input XML
```xml
<configuration>
    <database>
        <property name="url" description="Database connection URL"/>
        <property name="timeout" description="Connection timeout in seconds"/>
    </database>
    <application>
        <title>User Management System</title>
        <description>System for managing user accounts</description>
    </application>
</configuration>
```

### Output Labels
```
CONFIG_DATABASE_URL=Database connection URL
CONFIG_CONNECTION_TIMEOUT=Connection timeout in seconds
CONFIG_USER_MANAGEMENT_SYSTEM=User Management System
CONFIG_USER_ACCOUNT_MANAGEMENT=System for managing user accounts
```

## Supported XML Patterns
- **Element Text Content**: `<title>Save Changes</title>`
- **Attribute Values**: `<button title="Click to Save"/>`
- **CDATA Sections**: `<![CDATA[Error Message]]>`
- **Nested Elements**: `<menu><item>File Options</item></menu>`
- **Description Attributes**: `<property description="Configuration value"/>`

## Configuration Options
- **Custom Prefixes**: Override default prefixes for specific file types
- **Target Files**: Specify custom msgbases file locations
- **Exclusion Patterns**: Skip certain XML elements or attributes
- **Naming Rules**: Apply custom naming conventions per project

## Error Handling
- **Malformed XML**: Graceful handling with detailed error reporting
- **File Access Issues**: Clear error messages for permission problems
- **Backup Failures**: Prevent processing if backups cannot be created
- **Rollback Capability**: Restore from backups if processing fails

## Quality Assurance
- **Pre-processing Validation**: Verify XML structure and file access
- **Post-processing Verification**: Confirm all files updated correctly
- **Format Consistency**: Ensure output meets project standards
- **Integration Testing**: Validate with actual project XML files