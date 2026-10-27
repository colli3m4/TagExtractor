# TagExtractor

TagExtractor reads a UTF-8 literature text file, removes configurable stop words,
counts the remaining words, and writes the 100 most frequent tags to a tab-separated
output file. Gutenberg start/end markers are used to exclude the book's download
header and footer when present.

## Run in IntelliJ IDEA

Run `Main`. With no arguments, it reads `data/pride-and-prejudice.txt` and
`data/stopwords.txt`, then writes `output/tags.txt`. Edit the stop-words file and
run again to tune the final tag list.

To use other files, pass exactly three program arguments:

```text
<input.txt> <stopwords.txt> <output.txt>
```

The output contains a `tag` / `count` header and is sorted by descending frequency,
then alphabetically for ties.

## Included literature

`data/pride-and-prejudice.txt` is *Pride and Prejudice* by Jane Austen, downloaded
from Project Gutenberg eBook #1342. The work is public domain in the United States.
See https://www.gutenberg.org/ebooks/1342 for the source and distribution details.
