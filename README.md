Hi everyone,

We're working on a project designed to help identify and track griefers in Don't Starve Together. The program analyzes screenshots for Steam profiles and compares them against a list of possible griefers. We're currently in the beta testing phase and looking for players to help test and improve the tool.

If you're interested in helping us make the DST community a safer place, we'd love to have you on board! Your feedback will be invaluable in refining the tool and making it more effective.

If you want to help by recording griefers, follow my guide (and PLEASE try to make videos and not screenshots): https://telegra.ph/Reporting-Guide-09-07

What the Program Does:
Function: The program detects Steam profiles and character icons in screenshots and then checks these profiles to see if they match with steam profiles contained in warnlist.txt.
IMPORTANT: Do NOT assume players in warnlist.txt are all griefers, especially if profile has no current reports, however pay attention and be ready to record if they do grief.

Detection Methods:
Multiple Players: When SOLO_TARGETING is set to FALSE, the program will take a screenshot 2 seconds after you start the analysis. If no icons are detected, it will return to the options menu.
Single Player: When SOLO_TARGETING is set to TRUE, the program will take a screenshot 3 seconds after you start the analysis and require you to hover over the icon of player to check. If it doesn’t detect the icon, it will wait 10 seconds before returning to the options menu.

Instructions:
Info Folder: Contains detailed instructions on how to use the program.
warnlist.txt: A list of possible griefers. If you detect anyone with a 'warning', please try to gather evidence and not kick immediately (else i cant have reports showing evidence of griefing)
graylist.txt : A list of people you analyzed in the past
Adding manually steamids to warnlist.txt is not suggested, though possible. You can always download the official version of warnlist.txt from the application itself or manually from this repository.

Feedback: Please contact me directly on Reddit (u/Leowanaton), Steam (Yocxhell) or Discord (_yocxhell) .
Suggestions: Feel free to leave comments here.
Thank you for helping with the beta testing!
