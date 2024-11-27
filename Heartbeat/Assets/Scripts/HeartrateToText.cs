using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;

public class HeartrateToText : MonoBehaviour
{
    private TextMeshPro HeartrateText;
    // Start is called before the first frame update
    void Start()
    {
        HeartrateText = GetComponent<TextMeshPro>();
        HeartrateEventManager.OnHeartrateUpdate += SetHeartrateText;
    }

    private void SetHeartrateText(object sender, HeartrateEventArgs e) {
        HeartrateText.text = e.heartrate.ToString();
    }
}
