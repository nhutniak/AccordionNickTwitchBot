# AccordionNickTwitchBot

Note: the gradle build isn't setup completely.  You also need to have a copy of the Twitch-Bot-Library in your eclipse workspace in order to run the bot.

All other dependencies should be present.

## Generating eclipse project and classpath files

Run:
    ./gradlew cleanEclipse eclipse
    
and then import/refresh the project in eclipse.  You will need to add the "Twitch-Bot-Library" to the build path.
