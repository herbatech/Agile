# Agile Monetize SDK

To configure Agile Monetize SDK need to add 

Step 1. Add the JitPack repository to your build file                 

dependencies {
            implementation 'com.github.herbatech:Agile:x.x.x'
    }

Step 2. Add the dependency

Then Add it in your root build.gradle at the end of repositories:

allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
 

Note: If it is private repories then you have to enter  username credential into project level gradle

       maven {
                url "https://jitpack.io"
                credentials { username authToken }
        }

And set the authToken value into the gradle.properties

And now here you are ready to start code for Agile Monetize














class com.ads.agile.AgileLog

Allow you to define the instance of Agile Monetization along with initialization of log events.
It also keep track of every log which is made offline by the SDK.

public method

1.    Agile(Context context,FragmentActivity activity,AgileTransaction agileTransaction)

Default constructor with three parameter i.e. context , activity and instance of AgileTransaction class it’s should not null.
 
Parameter
context
Context : instance of Context.
activity
FragmentActivity: object of FragmentActivity class.
agileTransaction
AgileTransaction : object of AgileTransaction class.

this field could be null , if you don’t want to include transaction mechanism into your application



2.    trackLog(final String eventType, final String appId)
 
Parameter
eventType
String : define the type of event
appId
String : It is application id which is provided by us to the developer


3.     getCount()

    Return the number of row from the local database.

5.    syncLog()
    It perform background operation to synchronise local log entry to server log.





6.    set(String key, int value)

Add extended data to the AgileLog class. The name must include a package prefix, for example the app com.companyname.example would use names like "com.android.demo.Example".

Parameter
name
String  : The name of the extra data, with package prefix.
value
Int : The Int data value.


7.    set(String key, float value)

Add extended data to the AgileLog class. The name must include a package prefix, for example the app com.companyname.example would use names like "com.android.demo.Example".

Parameter
name
String  : The name of the extra data, with package prefix.
value
float : The float data value.

8.    set(String key, long value)

Add extended data to the AgileLog class. The name must include a package prefix, for example the app com.companyname.example would use names like "com.android.demo.Example".

Parameter
name
String  : The name of the extra data, with package prefix.
value
long : The long data value.

9.    set(String key, String value)

Add extended data to the AgileLog class. The name must include a package prefix, for example the app com.companyname.example would use names like "com.android.demo.Example".

Parameter
name
String  : The name of the extra data, with package prefix.
value
String : The String data value.

10.    set(String key, boolean value)

Add extended data to the AgileLog class. The name must include a package prefix, for example the app com.companyname.example would use names like "com.android.demo.Example".

Parameter
name
String  : The name of the extra data, with package prefix.
value
boolean : The boolean data value.

11.    set(String key, short value)

Add extended data to the AgileLog class. The name must include a package prefix, for example the app com.companyname.example would use names like "com.android.demo.Example".

Parameter
name
String  : The name of the extra data, with package prefix.
value
short : The short data value.

12.    unset(String value)

    Call this method to remove the data from the object based on the key from existing object.

13.    getLogEvent()

Return of current object of String data type.

14.    clearLogEvent()

    This method will clear the existing log object.



class com.ads.agile.utils.AgileTransaction

This class provide the functionality to achieve the transaction into application. Which support initialization , rollback and commit method of transaction. In addition also provide method to clear or terminate the existing  transaction from the application as per the requirement.

public method

1.    AgileTransaction(Context context, FragmentActivity activity, String eventType, String appId)

Create an parametric constructor to initialize the transaction into application. This method call once when initialize as well as start the transaction. Which include additional information such a evenType and AppId. evenType and AppId won’t require when you are continue your transaction to some another activities or fragments.

Parameter
context
Context : instance of Context class
activity
FragmentActivity : instance of FragmentActivity class
eventType
String : pass the event Type before start any transaction.
appId
String : pass the application id






2.    AgileTransaction(Context context,FragmentActivity activity)

    Call this parameter when you resume your transaction to activities or fragments, here you don’t need to add additional two param since they only required when you init the transactions.

Parameter
context
Context : instance of Context class
activity
FragmentActivity : instance of FragmentActivity class

3.    set(String key, int value)

Add extended data to the AgileTransaction class. The name must include a package prefix, for example the app com.companyname.example would use names like "com.android.demo.Example".

Parameter
name
String  : The name of the extra data, with package prefix.
value
Int : The Int data value.

4.    set(String key, float value)

Add extended data to the AgileTransaction class. The name must include a package prefix, for example the app com.companyname.example would use names like "com.android.demo.Example".

Parameter
name
String  : The name of the extra data, with package prefix.
value
Float: The Float data value.






5.    set(String key, long value)

Add extended data to the AgileTransaction class. The name must include a package prefix, for example the app com.companyname.example would use names like "com.android.demo.Example".

Parameter
name
String  : The name of the extra data, with package prefix.
value
long : The long data value.


6.    set(String key, String value)

Add extended data to the AgileTransaction class. The name must include a package prefix, for example the app com.companyname.example would use names like "com.android.demo.Example".

Parameter
name
String  : The name of the extra data, with package prefix.
value
String : The IString nt data value.



7.    set(String key, boolean value)

Add extended data to the AgileTransaction class. The name must include a package prefix, for example the app com.companyname.example would use names like "com.android.demo.Example".

Parameter
name
String  : The name of the extra data, with package prefix.
value
Boolean : The Boolean data value.







8.    set(String key, short value)

Add extended data to the AgileTransaction class. The name must include a package prefix, for example the app com.companyname.example would use names like "com.android.demo.Example".

Parameter
name
String  : The name of the extra data, with package prefix.
value
short : The short data value.







9.    unset(String value)

To remove the existence data from the transaction call this method with the below following parameter.

Parameter
value
String  : The name of the extra data, with package prefix.


10.    getTransaction()

Method will return the object of transaction.

Returns
String
String  : return this transaction object.


11.    rollbackTransaction()

Call this method to rollback the existing transaction, start from the beginning. This method will reset the whole transaction to fresh one. After calling this method you need to re-initialize the transaction again to begin it.

12.    commitTransaction()

This method send the whole transaction to the server and prepare the transaction for new fresh process. After calling this method you need to re-initialize the transaction again to begin it.

13.    clearTransaction()

If developer don’t want to keep the existing transaction then in such situation clearTransaction()  method will clear the existing transaction. After calling this method you need to re-initialize the transaction again to begin it.

Synchronise Log from local database to Server database

Implement NetworkCallBack interface within Fragment or Activity of your application.

Public interface NetworkCallBack

Initialize the interface with following code 

new AgileStateMonitor(this).enable(getApplicationContext());

Public method

Public void syncLog() : method will synchronise the local database with server database

Override method

Override below method to enable the synchronise feature from local database into the server database.

public void onConnected()    : call this method when device is connected to internet and perform synchronize operation.by calling syncLog() method into it.

public void onDisconnected()    : call this method when device is not connected to internet.
