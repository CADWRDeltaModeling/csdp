use strict 'vars';

my $infile="diffdiff.txt";
open(IN, $infile) || die "can't open input file\n";
open(OUT, ">$infile.out") || die "can't open output file\n";

while(my $line=<IN>){
    if(index($line, "differ")>=0){
	print OUT $line;
    }
}
