#FAST→mix'n'match

Converts a database dump from http://www.oclc.org/research/themes/data-science/fast/download.html into the TSV (tab-separated value) format expected by https://tools.wmflabs.org/mix-n-match/ .

Currently it only converts the data about persons.

To run it, download the MARCXML data from http://www.oclc.org/research/themes/data-science/fast/download.html and unzip it into `src/main/resources/`. Then run `./gradlew run` from the root directory of this repo. This might take some time.
