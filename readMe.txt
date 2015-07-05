Screen Saver by Zhao Y. Li
Student ID: 20473475

"make all" to compile/run.

** Mouse **
drag - to move image

** Key ** - effective when pressed when mouse is in window (not animating)
UP key - increase size
DOWN key - decrease size
N key - creat new node as child of the node currently selected
S key - change to another shape, keep on pressing to choose from heart, star,rectangle
C key - change selected node to a random colour, keep on pressing until desired colour appear

**bonus feature**
H key - enter heart beat mode
E key - quit heart beat mode
RIGHT key - make the current node's colour darker (fade out)
LEFT key - make the current node's colour brighter (fade in)
B key - increase velocity of all nodes (max velocity exists)
V key - decrease velocity of all nodes (min velocity exists)
D key - delete selected node if it's not root

**HeartBeatMode**
All nodes' shape will change to heart and their colour will be a random shade of pink.
The nodes's size will increase and decrease to mimic hearts beating. A new node will
be added as the children of the root every heat beat, the size of all nodes will 
decrease gradually. This mode starts when you press H key and moves mouse out of window,
and will stop when you press E key.

Notes:
1.the changes in velocity is not visible until animation starts. Velocity should
  be changed when animation is stopped. If changed during animation, the change
  will take effect at next animation event.

2. no node should be selected when changing velocity.


