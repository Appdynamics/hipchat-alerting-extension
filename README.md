hipchat-alerting-extension
==========================

This alerting extension is only meant for on-premise controllers.

## Use Case

The HipChat alerting extension enables AppDynamics to post custom notifications as messages in a provided HipChat room. Chat room members can see a brief description of the health rule violation or event and get more detail on AppDynamics by following the URL provided in the alert message.

### Prerequisites

- You have a HipChat Account.
- You have a chat room created (and active) to send alert messages to.
- You have either admin or notifications auth_token authorized to make hipchat API calls.

### Steps

1. Run "mvn clean install"
2. Download and unzip the file 'target/hipchat-alert.zip'
or Download the HIPCHAT Alerting Extension zip from  http://appsphere.appdynamics.com/t5/AppDynamics-eXchange/HipChat-Alerting-Extension/idi-p/7640
3. Create a folder/directory as <controller-install-path>custom\actions\hipchat-alert
4. Unzip the contents from zip (from step 2) in the above directory. You will see a prompt if you already have a custom.xml file in the /custom/actions/ directory. Don't let the unzip process overwrite it. Instead, merge the contents.
5. Specify the values the properties in config.properties
6. In directory = <controller-install-dir>\custom\actions add custom.xml, add below action in xml file(modify if the file already exists, and merge the below action)

 ``<custom-actions>
 	<action>
		<type>hipchat-alert</type>
       <!-- For windows *.bat -->
 		<executable>hipchat-alert.bat</executable>
 		<!-- For Linux/Unix *.sh -->
 		<!-- executable>hipchat-alert.sh</executable -->
 	</action>
 </custom-actions>``

7. UnComment the appropriate executable tag based on windows or linux/unix machine.

8. Installing Custom Actions:

      To create a Custom Action, first refer to the the following topics (requires login):
      * [Creating custom action](http://docs.appdynamics.com/display/PRO13S/Custom+Actions)
      * [Build an Alerting Extension](http://docs.appdynamics.com/display/PRO13S/Build+an+Alerting+Extension)

Now you are ready to use this extension as a custom action. In the AppDynamics UI, go to Alert & Respond -> Actions. Click Create Action. Select Custom Action and click OK. In the drop-down menu you can find the action called 'hipchat-alert'.

##Contributing

Find out more in the [AppSphere](http://appsphere.appdynamics.com/t5/AppDynamics-eXchange/HipChat-Alerting-Extension/idi-p/7640) community.

##Support

For any questions or feature request, please contact [AppDynamics Center of Excellence](mailto:ace-request@appdynamics.com).


