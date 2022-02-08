# Simulator for UnleashTheGeek - 2019

## Introduction

Along with my amazing team, I participated in the **UTG 2019** where the problem statement was very fascinating.<br />
In an arena, there were **Ores** buried which we had to mine using our **Robots**.<br />
In order to know where the Ores were buried, we need to deploy **Radar** which we can basically get it from the base camp (left most point).<br />
We could also trap the robots of the opponent team with the help of **Bomb** deployment.<br />
There are a lot of more descriptive details which were provided to us in the **Codingame platform**.<br />

Our team had planned that we'll proceed with creation of a simulator in case it's possible for the particular problem statement.<br />
Fortunately, simulation was possible for the problem statement. I worked on creating the simulator while my other 2 team members worked on the Player code.<br />
I've created this repository to share the **simulator (code inside 'simulator' package)** which was created in about **6-7 hours**.<br />

<br>

You can follow the below blog post on the Codingame platform about the UTG:<br /> 
https://www.codingame.com/blog/mysteries-amadeus-coding-challenge-revealed/

Also you can check out the below games to see the battles:<br />
https://www.codingame.com/contests/unleash-the-geek-amadeus/leaderboard/global

<br>

### Below is a screenshot of the arena which we simulate in repository.

![CodinGame Arena](https://drive.google.com/uc?export=view&id=1rLfDMbAJmTD-tgdjri1xZfTUdejBnaxI)

<br>
<br>

### Representation of the whole grid using the below format:

<br>

#### The entities are represented as follows:<br />
<br />
O - Ore<br />
R - Radar<br />
B - Bot(Robot)<br />
T - Trap(Bomb)<br />
H - Hole<br />
.  - No Hole<br />
<br />
Some examples are given below:<br />
<br />
O5 - Ore with 5 units of amount<br />
B1 - Robot with ID 1<br />
<br />
<br />


### Round 1

![Round 1](https://drive.google.com/uc?export=view&id=1jDmTc6fIybPHPWK28lHatGSVDFwUgcwP)

<br>

#### Some inference from the above image:

* Robot with ID 4 and 9 start at cell (2,0)
* 9 units of Ore is present in cell (1,1)

<br>

-----------

<br>
 

### Round 10

![Round 10](https://drive.google.com/uc?export=view&id=1_uKZgs-lGzRhAbmOL7F6tUrrQ50nWFL9)

<br>

#### Some inference from the above image:

* Robot with ID 4 is at cell (2, 2)
* Trap has been put at cell (1, 9)
* Radar has been put at cell (5, 4) and (5, 12)

<br>

-----------

<br>


### Round 200

![Round 200](https://drive.google.com/uc?export=view&id=1l0v2ehlNctEY3m1HCOKzffos3QVgPjzn)

<br>

#### Some inference from the above image:

* Robot with ID 2 is at cell (1, 6)
* Radar has been put at cell (1, 11), (1, 12) and (10, 9)
* Most of the Ore has been mined by the Robots

<br>

-----------

<br>

### Results

#### We get the below output in the console :  <br />

<br>

##### *****************  RESULT ***************** <br />
##### Old Player wins 25 matches <br />
##### New Player wins 445 matches <br />
##### Draw  30 matches <br />
##### *****************  RESULT ***************** <br /> 

<br>
<br>

#### This confirms that the latest player code performs better than the old code. <br />
#### In the above example, 500 matches of 200 round each were played between NewPlayer(version 2) and OldPlayer(version 1)<br />
 
