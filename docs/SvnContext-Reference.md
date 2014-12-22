# `SvnCheckoutStrategy` enum reference

The SvnCheckoutStrategy enumeration contains the available checkout strategies provided by the Subversion Jenkins plugin.

----
#### `Update`

Use 'svn update' whenever possible, making the build faster. But this causes the artifacts from the previous build to remain when a new build starts.

----
#### `Checkout`

Delete everything first, then perform "svn checkout". While this takes time to execute, it ensures that the workspace is in the pristine state.

----
#### `UpdateWithClean`

Jenkins will first remove all the unversioned/modified files/directories, as well as files/directories ignored by "svn:ignore", then execute "svn update". This emulates the fresh check out behaviour without the cost of full checkout.

----
#### `UpdateWithRevert`

Do 'svn revert' before doing 'svn update'. This slows down builds a bit, but this prevents files from getting modified by builds.

# `svn` closure reference.

----
#### `location(String svnUrl, String localDir = '.')`

At least one location MUST be specified. Additional locations can be specified by calling location() multiple times.

`svnUrl`   The URL of the repositoy to be checked out.  
`localDir` Destination directory of checkout, relative to workspace. If not specified, defaults to '.'.

----
#### `checkoutStrategy(SvnCheckoutStrategy strategy)`

The checkout strategy that should be used.  This is a global setting for all locations. If no checkout strategy is configured, the default is SvnCheckoutStrategy.Update.

`strategy` Strategy to use. See [SvnCheckoutStrategy](#svncheckoutstrategy-enum-reference)

----
#### `excludedRegion(String pattern)`

Add an excluded region.  Each call to excludedRegion() adds to the list of excluded regions. If excluded regions are configured, and Jenkins is set to poll for changes, Jenkins will ignore any files and/or folders that match the specified patterns when determining if a build needs to be triggered.

`pattern` A regular expression that should be matched as part of the excluded regions.

----
#### `excludedRegions(String... patterns)`

Add a list of excluded regions.  Each call to excludedRegions() adds to the list of excluded regions. If excluded regions are configured, and Jenkins is set to poll for changes, Jenkins will ignore any files and/or folders that match the specified patterns when determining if a build needs to be triggered.

`patterns` A list of regular expressions that should be matched as part of the excluded regions.

----
#### `excludedRegions(Iterable<String> patterns)`

Add a list of excluded regions.  Each call to excludedRegions() adds to the list of excluded regions. If excluded regions are configured, and Jenkins is set to poll for changes, Jenkins will ignore any files and/or folders that match the specified patterns when determining if a build needs to be triggered.

`patterns` A list of regular expressions that should be matched as part of the excluded regions.

----
#### `includedRegion(String pattern)`

Add an included region.  Each call to includedRegion() adds to the list of included regions. If included regions are configured, and Jenkins is set to poll for changes, Jenkins will ignore any files and/or folders that do _not_ match the specified patterns when determining if a build needs to be triggered.

`pattern` A regular expression that should be matched as part of the included regions.

----
#### `includedRegions(String... patterns)`

Add a list of included regions.  Each call to includedRegions() adds to the list of included regions. If included regions are configured, and Jenkins is set to poll for changes, Jenkins will ignore any files and/or folders that do _not_ match the specified patterns when determining if a build needs to be triggered.

`patterns` A list of regular expressions that should be matched as part of the included regions.

----
#### `includedRegions(Iterable<String> patterns)`

Add a list of included regions.  Each call to includedRegions() adds to the list of included regions. If included regions are configured, and Jenkins is set to poll for changes, Jenkins will ignore any files and/or folders that do _not_ match the specified patterns when determining if a build needs to be triggered.

`patterns` A list of regular expressions that should be matched as part of the included regions.

----
#### `excludedUser(String user)`

Add an excluded user.  Each call to excludedUser() adds to the list of excluded users. If excluded users are configured, and Jenkins is set to poll for changes, Jenkins will ignore any revisions committed by the specified users when determining if a build needs to be triggered.

`user` User to ignore when triggering builds.

----
#### `excludedUsers(String... users)`

Add a list of excluded users.  Each call to excludedUsers() adds to the list of excluded users. If excluded users are configured, and Jenkins is set to poll for changes, Jenkins will ignore any revisions committed by the specified users when determining if a build needs to be triggered.

`users` A list of users to ignore when triggering builds.

----
#### `excludedUsers(Iterable<String> users)`

Add a list of excluded users.  Each call to excludedUsers() adds to the list of excluded users. If excluded users are configured, and Jenkins is set to poll for changes, Jenkins will ignore any revisions committed by the specified users when determining if a build needs to be triggered.

`users` A list of users to ignore when triggering builds.

----
#### `excludedCommitMsg(String pattern)`

Add an exluded commit message.  Each call to excludedCommitMsg() adds to the list of excluded commit messages. If excluded messages are configured, and Jenkins is set to poll for changes, Jenkins will ignore any revisions with commit messages that match the specified patterns when determining if a build needs to be triggered.

`pattern` A regular expression that should be matched as part of the excluded commit messages.

----
#### `excludedCommitMsgs(String... patterns)`

Add a list of excluded commit messages.  Each call to excludedCommitMsgs() adds to the list of excluded commit messages. If excluded messages are configured, and Jenkins is set to poll for changes, Jenkins will ignore any revisions with commit messages that match the specified patterns when determining if a build needs to be triggered.

`patterns` A list of regular expressions that should be matched as part of the excluded commit messages.

----
#### `excludedCommitMsgs(Iterable<String> patterns)`

Add a list of excluded commit messages.  Each call to excludedCommitMsgs() adds to the list of excluded commit messages. If excluded messages are configured, and Jenkins is set to poll for changes, Jenkins will ignore any revisions with commit messages that match the specified patterns when determining if a build needs to be triggered.

`patterns` A list of regular expressions that should be matched as part of the excluded commit messages.

----
#### `excludedRevProp(String revisionProperty)`

Set an excluded revision property. If an excluded revision property is set, and Jenkins is set to poll for changes, Jenkins will ignore any revisions that are marked with the specified revision property when determining if a build needs to be triggered. This only works in Subversion 1.5 servers or greater.

`revisionProperty` The revision property checked when triggering builds.

----
#### `browserCollabNet(String url)`

Configure the job to use the CollabNet browser.

`url` The repository browser URL for the root of the project. For example, a Java.net project called myproject would use https://myproject.dev.java.net/source/browse/myproject.

----
#### `browserFishEye(String url, String rootModule = '')`

Configure the job to use the FishEye browser.

`url` Specify the URL of this module in FishEye (such as http://fisheye6.cenqua.com/browse/ant/).  
`rootModule` Specify the root Subversion module that this FishEye monitors. For example, for http://fisheye6.cenqua.com/browse/ant/, this field would be ant because it displays the directory "/ant" of the ASF repo. If FishEye is configured to display the whole SVN repository, leave this field empty.

----
#### `browserSvnWeb(String url)`

Configure the job to use the SVN::Web browser.

`url` Specify the URL of this module in SVN::Web.

----
#### `browserSventon(String url, String repoInstance)`

Configure the job to use the Sventon 1.x browser.

`url` Specify the URL of the Sventon repository browser. For example, if you normally browse from http://somehost.com/svn/repobrowser.svn?name=local, this field would be http://somehost.com/svn/.  
`repoInstance` Specify the Sventon repository instance name that references this subversion repository. For example, if you normally browse from http://somehost.com/svn/repobrowser.svn?name=local, this field would be local.

----
#### `browserSventon2(String url, String repoInstance)`

Configure the job to use the Sventon 2.x browser.

`url` Specify the URL of the Sventon repository browser. For example, if you normally browse from http://somehost.com/svn/repobrowser.svn?name=local, this field would be http://somehost.com/svn/.
`repoInstance` Specify the Sventon repository instance name that references this subversion repository. For example, if you normally browse from http://somehost.com/svn/repobrowser.svn?name=local, this field would be local.

----
#### `browserViewSvn(String url)`

Configure the job to use the ViewSVN browser.

`url` Specify the root URL of ViewSVN for this repository (such as http://svn.apache.org/viewvc).

----
#### `browserWebSvn(String url)`

Configure the job to use the WebSVN browser.

`url` Specify the URL of this module in WebSVN.

----
#### `configure(Closure withXmlClosure)`

Sets a closure to be called when the XML node structure is created. The SVN node is passed to the closure as the first parameter.

`withXmlClosure` Closure used to perform additional configuration on the generated SVN node. The closure is passed the SVN SCM.
