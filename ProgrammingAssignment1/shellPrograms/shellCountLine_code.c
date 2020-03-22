#include "shellPrograms.h"

/*
Count the number of lines in a file 
*/
int shellCountLine_code(char **args)
{

    /** TASK 6  **/
    // ATTENTION: you need to implement this function from scratch and not to utilize other system program to do this
    // 1. Given char** args, open file in READ mode based on the filename given in args[1] using fopen()
    // 2. Check if file exists by ensuring that the FILE* fp returned by fopen() is not NULL
    // 3. Read the file line by line by using getline(&buffer, &size, fp)
    // 4. Loop, as long as getline() does not return -1, keeps reading and increment the count
    // 6. Close the FILE*
    // 7. Print out how many lines are there in this particular filename
    // 8. Return 1, to exit program
    char *line_buf = NULL;
    size_t line_buf_size = 0;
    int line_count = 0;
    ssize_t finp;
    FILE* fp = fopen(args[1], "r");
    if(!fp) exit(1);
    finp = getline(&line_buf, &line_buf_size, fp);
    while (finp >= 0)
    {   
        line_count++;
        finp = getline(&line_buf, &line_buf_size, fp);
    }
    free(line_buf);
    line_buf = NULL;
    fclose(fp);
    printf("There are %d lines in %s \n", line_count, args[1]);
    return 1;
}

int main(int argc, char **args)
{
    return shellCountLine_code(args);

}