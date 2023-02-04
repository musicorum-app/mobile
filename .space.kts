job("Build and release to internal testing") {
    startOn {
        gitPush { enabled = true }
    }

    container("Build and publish to internal", "musicorum.registry.jetbrains.space/p/main/containers/android-publisher:latest") {
        env["GOOGLE_SA_KEY"] = Secrets("google_sa_key")
        env["KEY_STORE"] = Secrets("key_store_password")
        env["KEY_STORE_PASSWORD"] = Secrets("key_store_password")
        env["KEY_PASSWORD"] = Secrets("key_password")
        env["KEY_ALIAS"] = Params("key_alias")

        shellScript {
            content = """
                echo Get private signing key...
                echo ${'$'}KEY_STORE > upload_key.hex
                xxd -plain -revert upload_key.hex  upload_key.jks
                echo Get Google service account key...
                echo ${'$'}GOOGLE_SA_KEY > google_sa_key.hex
                xxd -plain -revert google_sa_key.hex  google_sa_key.json
                echo Get tokens.release.properties...
                echo ${'$'}BUILD_CONFIG_PROPERTIES > tokens.release.hex
                xxd -plain -revert tokens.release.hex  tokens.release.properties
                cp ./tokens.release.properties ./tokens.properties
                echo cat ./tokens.release.properties
                echo Get google-services.json...
                echo ${'$'}GOOGLE_SERVICES_JSON > google_services.hex
                xxd -plain -revert google_services.hex google-services.json
                mv ./google-services.json ./app/google-services.json
                echo Get crowdin.properties...
                echo ${'$'}CROWDIN_PROPERTIES > crowdin_properties.hex
                xxd -plain -revert crowdin_properties.hex crowdin.properties
                echo Build and pulbish AAB...
                ./gradlew publishBundle
            """
        }
    }
}