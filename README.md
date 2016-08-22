# HiddenJournal
A simple journal application for Android, with option to hide entries with a password.

# Description
This journal is based on the theory of plausible deniability. The password acts as a filter: entering a wrong password gives no error message, it simply prevents the protected entries from being displayed. You may even have two or more passwords: a real one with your real entries, and a second one with a few fake entries that you created, just in case you are pressured to give up your password.
In order to prevent suspicion, installing this application will place a generic looking icon called "Journal" on your device, instead of its actual name "Hidden Journal".

That said, his app was not meant to fool the FBI. Someone with sufficient expertise will be able to extract all the information from the system drive.

Operation:
-entries with no password are always visible
-when creating a new entry, the currently entered password is used
-entering a password will display entries using that password, and all entries without a password set

# License
Distributed under MIT License, see the LICENSE file for details.

# Thanks
SundeepK for his <a href="https://github.com/SundeepK/CompactCalendarView">CompactCalendarView</a> library.