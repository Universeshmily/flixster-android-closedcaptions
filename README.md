Flixster Closed Captioning Library
==================================

Android library for handling closed captioning alongside video with appropriate SMTPE-TT/TTML files.  Please take a look at the following classes to get started:

###### CaptionedPlayer
A class that should be extended by the Activity you wish to control displaying captions.

###### CaptionPreferences
Holds all appropriate settings and preferences related to captions.  These settings can be modified or reset by calling the appropriate methods.

###### CaptionPreferenceStorage
If you wish preferences to be preserved across sessions, have a class implement these methods and pass the class to the CaptionPreferences instance, which will automatically attempt to save the state when any of the settings are changed.

###### CaptionView
An extension of a TextView that understands and applies the desired changes according to the CaptionPreferences.

###### CaptionLogger
An extension of the default android Log class.

Additionally, take a look at the example classes for a possible implementation.

**Note that this library is optimized for displaying captions alongside videos shown in full-screen on a device.  Captions may display incorrectly if the video window takes up only a small portion of a device's screen.**

License
-------
```
Copyright 2014 Flixster Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
