AppDynamics HipChat Alerting Extension

- This extension provides a way to send an alert message to a given HipChat Room.

- This is setup as a policy action in Controller (only for on-premise)

- Based on the events (health rule violation or other events) on which the policy action is setup, it will send a alert message to HipChat room.

- Alert Message formats will be:
A) Health rule violation = [P:<priority>, Severity:<severity>, App Name:<App Name>, Health Rule Name: <Health Rule Name>, Affected Entity Type:<Affected Entity Type>, Affected Entity Name:<Affected Entity Name>] URL:<incident url> 
B) Event = [P:<priority>, Severity:<severity>, App Name:<App Name>, Event Name:<Event Name>] URL:<incident url>

- The HipChat API call is preferably done over https (though an option to set http), if https calls fails due to certificate exception.
If this happens, then the trusted certificate needs to be stored in the keystore (in controller machine) following the below link:
http://letmehelpyougeeks.blogspot.com/2009/07/adding-servers-certificate-to-javas.html
The certificate can be downloaded from browser tools by browsing https://api.hipchat.com

- There are certain required properties needed to be set in config.properties.

Steps:
------

1) Download the HIPCHAT Alerting Extension zip from http://appsphere.appdynamics.com
2) Create a folder/directory as <controller-install-path>custom\actions\hipchat-alert
3) Unzip the contents from zip (from step 1) in the above directory
4) Modify the properties in config.properties

5) In folder/directory = <controller-install-path>custom\actions
add custom.xml (modify if the file already exists, and merge the below action)
add below action in xml file

<custom-actions>
	<action>
		<type>hipchat-alert</type>
		<!-- For windows *.bat -->
		<executable>hipchat-alert.bat</executable>
		<!-- For Linux/Unix *.sh -->
		<!-- executable>hipchat-alert.sh</executable -->
	</action>
</custom-actions>

6) This custom action will be available in controller as 'hipchat-alert' to select.