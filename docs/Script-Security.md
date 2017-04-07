Starting with version 1.60, Job DSL integrates with the
[Script Security plugin](https://plugins.jenkins.io/script-security) to provide a secure
environment for executing DSL scripts. This provides three options for configuring script security which are described
in detail below. You should consider these options carefully to choose the option that matches your approach to Job DSL.

Note: Script security for Job DSL is only available when security is enabled in Jenkins.


Script Approval
---------------

The first security system is to allow any kind of script to be run, but only with an administrator’s
approval. There is a globally maintained list of approved scripts which are judged to not perform any malicious actions.

When an administrator saves the seed job, any DSL scripts it contains are automatically added to the approved list. They
are ready to run with no further intervention. (“Saving” usually means from the web UI, but could also mean uploading a
new XML configuration via REST or CLI.)

When a non-administrator saves a seed job, a check is done whether any contained scripts have been edited from an
approved text. (More precisely, whether the requested content has ever been approved before.) If it has not been
approved, a request for approval of this script is added to a queue.

An administrator may now go to _Manage Jenkins » In-process Script Approval_ where a list of scripts pending approval
will be shown. Assuming nothing dangerous-looking is being requested, just click _Approve_ to let the script be run
henceforth.

If you try to run an unapproved script, it will simply fail, with a message explaining that it is pending approval. You
may retry once the script has been approved.

This options may not be feasible when checking out frequently changing DSL scripts from SCM since every change to the
scripts in SCM would require an approval from an administrator.


Groovy Sandboxing
-----------------

Waiting for an administrator to approve every change to a script, no matter how seemingly trivial, could be unacceptable
in a team spread across timezones or during tight deadlines. As an alternative option, the Script Security system lets
Groovy scripts be run without approval so long as they limit themselves to operations considered inherently safe. This
limited execution environment is called a sandbox.

To switch to this mode, simply check the box Use Groovy Sandbox in the _Process Job DSLs_ build step configuration.
Sandboxed scripts can be run immediately by anyone. (Even administrators, though the script is subject to the same
restrictions regardless of who wrote it.) When the script is run, every method call, object construction, and field
access is checked against a whitelist of approved operations. If an unapproved operation is attempted, the script is
killed and the corresponding Jenkins feature cannot be used yet.

The Script Security plugin ships with a small default whitelist, but you are not limited to the default whitelist. Every
time a script fails before running an operation that is not yet whitelisted, that operation is automatically added to
another approval queue. An administrator can go to the same page described above for approval of entire scripts, and see
a list of pending operation approvals. If _Approve_ is clicked next to the signature of an operation, it is immediately
added to the whitelist and available for sandboxed scripts.

Most signatures be of the form `method class.Name methodName arg1Type arg2Type…`, indicating a Java method call with a
specific “receiver” class (`this`), method name, and list of argument (or parameter) types. (The most general signature
of an attempted method call will be offered for approval, even when the actual object it was to be called on was of a
more specific type overriding that method.) You may also see `staticMethod` for static (class) methods, `new` for
constructors, and `field` for field accesses (get or set).

Administrators in security-sensitive environments should carefully consider which operations to whitelist. Operations
which change state of persisted objects (such as the global configuration) should generally be denied. Most
`getSomething` methods are harmless.

All Job DSL methods are whitelisted by default, but Jenkins access control checks are applied. These checks prevent
users from gaining elevated permissions through Job DSL scripts. For this to work, the DSL job needs to run as a
particular user. This is generally accomplished by installing and configuring the
[Authorize Project plugin](https://plugins.jenkins.io/authorize-project).

After installing Authorize Project plugin, you will find _Access Control for Builds_ in _Manage Jenkins » Configure
Global Security_. Adding _Project default Build Authorization_ or _Per-project configurable Build Authorization_ enables
the Authorize Project plugin.

Choosing _Per-project configurable Build Authorization_ allows the authentication that a job will run as to be
configured from the job configuration page. A new side bar menu _Authorization_ will appear in job pages. The
following strategies are available and can be restricted:

* _Run as the user who triggered the build_: Does what the label says, but does not work for scheduled or polled builds.
* _Run as anonymous_: Runs the build as the special user 'anonymous' which represents unauthenticated users. This is
  typically useless for Job DSL.
* _Run as the specified user_: You are requested to enter the password of the specified user except in the following cases:
  * You are an administrator.
  * You are the specified user.
  * The specified user is not changed from the last configuration, and "No need for re-authentication" is checked. This
    can threaten your Jenkins security. Be careful to use.
* _Run as SYSTEM_: This options disables Authorize Project plugin. This is useless for Job DSL since the build will be
  rejected.

When choosing _Project default Build Authorization_, the same strategy is applied to all jobs. One of the strategies
mentioned above can be selected.

Refer to the documentation of the
[Script Security plugin](https://plugins.jenkins.io/script-security) and the
[Authorize Project plugin](https://plugins.jenkins.io/authorize-project) for more details.

Note that some operators in [[configure blocks|The Configure Block]] are not available when running in the restricted
sandbox.


Disabling Script Security
-------------------------

Job DSL script security can be disabled on the _Configure Global Security_ page. But this decision should be taken with
great care and only if the consequences are well understood as it would allow users to run arbitrary code within the
Jenkins process (even changing its security settings or terminating the process) and change any state of persisted
objects.
