import ExpandNodeToolBar from '@/pages/Graph/Design/types/ExpandNodeToolBar';
import { openPanel } from '@/utils/FormUtils';
import { Icon } from '@iconify/react';
import { Handle, Position } from '@xyflow/react';
import { Flex } from 'antd';
import React, { useState } from 'react';
import './base.less';

const customNodeFormSchema = {
  type: 'object',
  properties: {
    aaa: {
      type: 'string',
      title: 'input 1',
      required: true,
      'x-decorator': 'FormItem',
      'x-component': 'Input',
    },
  },
};

interface ICustomNodeFormData {
  aaa: string;
}

type props = {
  data: any;
};
const StartNode: React.FC<props> = ({ data }) => {
  const [formData, setFormData] = useState<ICustomNodeFormData>();
  const onClick = () => {
    openPanel<ICustomNodeFormData>(customNodeFormSchema, {
      onConfirm: (values) => setFormData(values),
      data: formData,
    });
  };
  return (
    <>
      <div
        onClick={onClick}
        style={{
          width: data.width,
          height: data.height,
        }}
      >
        <ExpandNodeToolBar></ExpandNodeToolBar>
        <Flex vertical={true} className="cust-node-wrapper">
          <div className="node-type">
            <Icon
              style={{
                marginBottom: '-2px',
              }}
              icon="material-symbols:android-safety-outline"
            ></Icon>
            Custom Node
            {data.label}
          </div>
        </Flex>
        <Handle
          type="source"
          position={Position.Right}
          className={'graph-node__handle'}
        ></Handle>
        Start
      </div>
    </>
  );
};

export default StartNode;
