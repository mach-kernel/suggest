# suggest
A stab at implementing a Google-style typeahead suggestion from a dump of previous queries. Written in pure Java 8 with no external dependencies.

## Getting Started

```bash
wget http://www.cim.mcgill.ca/~dudek/206/Logs/AOL-user-ct-collection/aol-data.tar.gz
tar -xvf aol-data.tar.gz
gunzip AOL-user-ct-collection/user-ct-test-collection-01.txt.gz
# Skip the first line, and take the 
tail -n +2 AOL-user-ct-collection/user-ct-test-collection-01.txt | cut -f2 > justquery.txt
gradle --console plain run
```

```
suggest> loadfile justquery.txt
suggest> iphone

```

## Data

To keep things easy, loading data is limited to reading newline delimited text files. It is surprisingly hard to find these kinds of datasets. The list of data used to test this program are listed below:

- [Web Search Query Logs](https://jeffhuang.com/search_query_logs.html)
  - The AOL mirror is the only live one

## Methodology

- Google defaults to showing 10 suggestions, so we will do the same.
- The rank of the suggestion will be determined by the frequency of each unique query.