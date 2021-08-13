# Xray Update Checker


##### Tool to check tests updated in jira from a Xray import request

This tool allows the xray-endpoint-update jenkinsfile script to check which tests has been updated or uploaded in the last
Xray import request.

To know more about the xray-endpoint-update tool, check the TAS seed's "Xray automated upload" readme sections:

[FE seed readme](https://tools.adidas-group.com/bitbucket/projects/TE/repos/seed-tas-serenitybdd-fe-web/browse/README.md)

[BE seed readme](https://tools.adidas-group.com/bitbucket/projects/TE/repos/seed-tas-serenitybdd-be-restapi/browse/README.md)

## How it works
The tool uses the Xray import response (../issuesList.tmp), that contains the ids of all the test scenarios uploaded using its API (not just the new ones), 
and then uses the Jira API to check the update date of each scenario in Jira, and compares it with the time of the xray import. 
Then, the ids of the updated tests are saved into a file (../updatedTests.tmp), so the xray-endpoint-update jenkinsfile script can read them.
 
 ***
 #### Testing the tool and feedback
 
 This tool was developed as a solo side project. Therefore, it's testing has been limited.
 ***
 
 Developed by Mario Torbado
