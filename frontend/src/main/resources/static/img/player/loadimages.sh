while read line; do
  curl $line > ${line##*/}
done < img_urls.txt