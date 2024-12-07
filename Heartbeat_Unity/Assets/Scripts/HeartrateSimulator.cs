using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class HeartrateSimulator : MonoBehaviour
{
    private HeartrateEventManager heartrateEventManager;

    // private float timer1 = 0f;

    [Range(50f, 200f)]
    public int TestHeartrate;
    private float timer1 = 0f;

    // Start is called before the first frame update
    void Start()
    {
        heartrateEventManager = new HeartrateEventManager();
    }

    // Update is called once per frame
    void Update() {
        if (Input.GetKeyDown(KeyCode.H)) {
        // timer1 += Time.deltaTime;
        // if(timer1 >= 1f){
        //     timer1 = 0f;
            TestHeartrate+=1;

            HeartrateEventArgs args = new HeartrateEventArgs();
            args.heartrate = TestHeartrate;
            Debug.Log("Updated Heartrate:"+args.heartrate);
            heartrateEventManager.UpdateHeartrate(args);
        }

        if (Input.GetKeyDown(KeyCode.A)) {
        // timer1 += Time.deltaTime;
        // if(timer1 >= 1f){
        //     timer1 = 0f;
            TestHeartrate-=1;

            HeartrateEventArgs args = new HeartrateEventArgs();
            args.heartrate = TestHeartrate;
            Debug.Log("Updated Heartrate:"+args.heartrate);
            heartrateEventManager.UpdateHeartrate(args);
        }
    }
}
