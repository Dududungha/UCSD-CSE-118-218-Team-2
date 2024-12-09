using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class TimerToDeactivate : MonoBehaviour
{
    public float seconds;
    public GameObject ObjectToDeactivate;
    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        if (seconds < 0) {
            ObjectToDeactivate.SetActive(false);
        }
        seconds -= Time.deltaTime;
    }
}
