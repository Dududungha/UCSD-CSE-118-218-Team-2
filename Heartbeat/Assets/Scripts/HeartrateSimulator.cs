using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class HeartrateSimulator : MonoBehaviour
{
    private HeartrateEventManager heartrateEventManager;

    [Range(50f, 200f)]
    public int TestHeartrate;

    // Start is called before the first frame update
    void Start()
    {
        heartrateEventManager = new HeartrateEventManager();
    }

    // Update is called once per frame
    void Update() {
        if (Input.GetKeyDown(KeyCode.H)) {
            HeartrateEventArgs args = new HeartrateEventArgs();
            args.heartrate = TestHeartrate;
            heartrateEventManager.UpdateHeartrate(args);
        }
    }
}
