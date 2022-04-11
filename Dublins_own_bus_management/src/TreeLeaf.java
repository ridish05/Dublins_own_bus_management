public class TreeLeaf
{
   //Variables which we need in order to initialize a TSTNode object.
   char data;
   Integer value;
   TreeLeaf left, mid, right;

   //The constructor for a TSTNode object.
   protected TreeLeaf(char data, Integer value) //New node with null links.
   {
     this.value = value;
     this.data = data;
     left = null;
     mid = null;
     right = null;
   }
}