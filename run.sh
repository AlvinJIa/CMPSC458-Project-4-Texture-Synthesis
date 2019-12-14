rm *.class
javac Synthesis.java
rm -rf output
mkdir output

input_files=(apples.jpg cans.jpg cells.jpg fire.JPG jellybeans.jpg tomatoes.jpg water.JPG wood.JPG)
texton_size=(47 49 51 53 55 57 59 61)
output_size=(200 230 260 290 320 350 380 400)
output_files=(apples.jpg cans.jpg cells.jpg fire.jpg jellybeans.jpg tomatoes.jpg water.jpg wood.jpg)


for ((i = 0; i < ${#input_files[@]}; ++i)); 
do
    echo java Synthesis  -input ./sampleTextures/${input_files[$i]} -texton-size ${texton_size[$i]} -output-size ${output_size[$i]} -output ./output/${output_files[$i]}
    java Synthesis  -input ./sampleTextures/${input_files[$i]} -texton-size ${texton_size[$i]} -output-size ${output_size[$i]} -output ./output/${output_files[$i]}
done