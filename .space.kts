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
        env["BUILD_CONFIG_PROPERTIES"] = Secrets("build_config_properties")
        env["GOOGLE_SERVICES_JSON"] = Secrets("google_services_json")
        env["CROWDIN_PROPERTIES"] = Secrets("crowdin_properties")

        env["AUTHORIZATION_SECRET"] = Secrets("file_repo_secret")

        shellScript {
            content = """
                echo Project key: ${'$'}GOOGLE_SERVICES_JSON
                echo Get private signing key...
                echo ${'$'}KEY_STORE > upload_key.hex
                xxd -plain -revert upload_key.hex upload_key.jks
                echo Get Google service account key...
                echo ${'$'}GOOGLE_SA_KEY > google_sa_key.hex
                xxd -plain -revert google_sa_key.hex google_sa_key.json
                echo Get tokens.release.properties...
                echo ${'$'}BUILD_CONFIG_PROPERTIES > tokens.release.hex
                xxd -plain -revert tokens.release.hex tokens.release.properties
                xxd -plain -revert tokens.release.hex tokens.properties
                echo Get google-services.json...
                echo ${'$'}GOOGLE_SERVICES_JSON > google_services.hex
                xxd -plain -revert google_services.hex google-services.json
                mv ./google-services.json ./app/google-services.json
                echo Get crowdin.properties...
                echo ${'$'}CROWDIN_PROPERTIES > crowdin_properties.hex
                xxd -plain -revert crowdin_properties.hex crowdin.properties
                echo Downloading fonts...
                mkdir ./app/src/main/res/font
                curl -f -L -H "Authorization: Bearer ${'$'}AUTHORIZATION_SECRET" -o ./app/src/main/res/font/author.zip https://files.pkg.jetbrains.space/musicorum/p/main/android-fonts/author/font.zip
                echo Unzipping fonts...
                cd ./app/src/main/res/font && unzip author.zip && cd
                rm ./app/src/main/res/font/author.zip
                echo Build and pulbish AAB...
                ./gradlew publishBundle
            """
        }
    }
}