# hackathon1 - claudiutodorut

The project is calculating and displaying in a google spreadsheet the following columns:
"IC ID", "IC Name", "IC SEM", "Day", "7 Hrs per day", "Deep Work Blocks", "Dev Time >70%", "Daily CiC", "Intensity - Focus 90%", "work blocks less than 1h", "Complient CiC",	"Actual Intensity", "Actual Focus".

Were added extra columns to be displayed: "Actual Intensity" and "Actual Focus".

All configuration is hold in application.yml file:
application:
  crossover:
    token: 430109:1564055978335:57f5167da6e473d9a1d41a9cf7b33dab
    teamId: 2952
    trackerApi: https://api.crossover.com/api/tracker/activity/groups?date={date}&fullTeam=true&groups=groups&refresh=false&teamId={teamId}&weekly=false
    checkInChatApi: https://api.crossover.com/api/productivity/managers/checkins?from={fromDate}&to={toDate}&teamId={teamId}
  candidate:
    hourPerDay: 7
    devTime: 70
    focusScore: 90
    intensityScore: 90
    deepWorkBlock: 3
    workBlocksLess: 6
    date: 2018-11-16 (yy-mm-dd)
  google:
    spreedsheetId: 1EuXuVLd1qioRBdypdtcTZ869QqKuz30r4yMbNwJ9cUA
    sheetName: Index

    
Project is using google api for writing in google sheet.
Data is writting in the following spreadsheet: https://docs.google.com/spreadsheets/d/1EuXuVLd1qioRBdypdtcTZ869QqKuz30r4yMbNwJ9cUA/edit#gid=0.

This can be modified by changing "spreedsheetId" in the application.yml file.

Note: Before starting the application it is needed that the spreedsheet to have created rows and have the same number and postion of the column as the spreadsheet above.