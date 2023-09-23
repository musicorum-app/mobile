(todo)

### Installing font
The app uses the Author font, which due licensing, we can't include it on this repository, that's why you will need to download it manually

1. Install the font family [here](https://www.fontshare.com/fonts/author).
2. Unzip the OTF fonts into `app/res/font`
3. Run this bash command to rename the files accordingly
```shell
for i in $( ls | grep [A-Z] );
do 
  mv -i $i `echo $i | tr [:upper:] [:lower:] | tr '-' '_'`;
done
```
