# GithubAPIAndroid

An Android app to display issues and their comments for a specific GitHub repository.
The app does the following: 

1. Uses the GitHub web api to retrieve all open issues associated with the Rails 
organization’s rails repository.
2. Displays a list of issues to the user.
a. Ordered by most-recently updated issue first.
b. Issue titles and the first 140 characters of the issue body are shown in the
list.
3. Allows the user to tap an issue to display a dialog containing all comments for that issue.
a. The complete comment body and user name of the comment author is shown.
b. All comments are displayed in the same text box, each separated by a separator.

# Assumptions taken :
1. The repository and organization names are hard coded.
2. Persisting data across multiple app launches is not done.
3. Refreshing the data while the app is running is not done - assumed that issues and
comments are static while the user has the app open.
4. The app only access the public information, so no authentication of any kind is required.

#Additional Info:
1. Documentation for the GitHub issues API can be found here:
http://developer.github.com/v3/issues/
2. The URL to fetch issues associated with the rails repo is:
https://api.github.com/repos/rails/rails/issues
