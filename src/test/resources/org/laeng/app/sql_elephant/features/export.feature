Feature: CSV data export
  As a amee profile import job
  I want to ensure I have the correct data exported

  Scenario: Correct fields, correct values
    Given The following database table:
      | id | varchar_col | varchar_col2 | integer_col | date_col              |
      | 1  | bert        | bert2        | 1111        | 2013-09-09 00:00:00.0 |
      | 2  | bob         | bob2         | 2222        | 2013-09-09 00:00:00.0 |
    When I run the data export
    Then I should output a batch dump file with the following values:
      | id | varchar_col | varchar_col2 | integer_col | date_col              |
      | 1  | bert        | bert2        | 1111        | 2013-09-09 00:00:00.0 |
      | 2  | bob         | bob2         | 2222        | 2013-09-09 00:00:00.0 |

  Scenario: Some inline quotes
    Given The following database table:
      | id | varchar_col | varchar_col2 | integer_col | date_col              |
      | 1  | bert's      | bert2 " else | 1111        | 2013-09-09 00:00:00.0 |
      | 2  | bob         | bob2         | 2222        | 2013-09-09 00:00:00.0 |
    When I run the data export
    Then I should output a batch dump file with the following values:
      | id | varchar_col | varchar_col2 | integer_col | date_col              |
      | 1  | bert's      | bert2 " else | 1111        | 2013-09-09 00:00:00.0 |
      | 2  | bob         | bob2         | 2222        | 2013-09-09 00:00:00.0 |

  Scenario: Null values
    Given The following database table:
      | id | varchar_col | varchar_col2 | integer_col | date_col              |
      | 1  | berts       |              | 1111        | 2013-09-09 00:00:00.0 |
      | 2  |             | bob2         | 2222        | 2013-09-09 00:00:00.0 |
    When I run the data export
    Then I should output a batch dump file with the following values:
      | id | varchar_col | varchar_col2 | integer_col | date_col              |
      | 1  | berts       |              | 1111        | 2013-09-09 00:00:00.0 |
      | 2  |             | bob2         | 2222        | 2013-09-09 00:00:00.0 |
