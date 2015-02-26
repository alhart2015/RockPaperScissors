
import subprocess
from time import sleep

def main():
	num_tournys = 50
	
	bot1 = raw_input("Please enter the name of the first bot: ")
	bot2 = raw_input("Please enter the name of the second bot: ")
	# bot1 = "MyocainePowder"
	# bot2 = "NashBot"
	cmd = "java Tournament " + bot1 + " " + bot2 + " 10000"

	outfile = open("tourny_results.txt", "w")
	print "Starting Tournament"
	for i in xrange(num_tournys):
		wait_process = subprocess.Popen(cmd, stdout=outfile, shell=True)

		while wait_process.poll() is None:
			sleep(1)


	outfile.close()

	infile = open("tourny_results.txt", "r")

	bot_one_wins = []
	bot_two_wins = []
	ties = []

	results = infile.readlines()
	for line in results:
		x = line.split(" ")
		if x[0] == bot1+":":
			bot_one_wins.append(x[1])
		elif x[0] == bot2+":":
			bot_two_wins.append(x[1])
		else:
			ties.append(x[1])

	b1avg = avg(bot_one_wins)
	b2avg = avg(bot_two_wins)
	print bot1, "average wins: ", b1avg
	print "Average ties: ", avg(ties)
	print bot2, "average wins: ", b2avg
	print "Margin of victory: ", abs(b1avg-b2avg)

	infile.close()
	


def avg(lst):
	total = 0
	for x in lst:
		total += int(x)
	return total/float(len(lst))



main()
