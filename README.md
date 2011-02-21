## How to build ##

Build apk

    mvn install

Build and sign apk, put signkey at $(SOURCE_DIR)/signkey.keystore and run

    mvn install -Djarsigner.storepass=KEYPASS

## Known Issue ##

